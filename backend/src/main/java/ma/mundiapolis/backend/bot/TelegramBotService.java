package ma.mundiapolis.backend.bot;

import ma.mundiapolis.backend.entities.*;
import ma.mundiapolis.backend.enums.BotState;
import ma.mundiapolis.backend.repositories.*;
import ma.mundiapolis.backend.services.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
public class TelegramBotService extends TelegramLongPollingBot implements BotMessageService {

    private final OpenAiService aiService;
    private final CustomerRepository customerRepo;
    private final AccountOperationRepository operationRepo;
    private final String botUsername;

    public TelegramBotService(@Value("${telegram.bot.token}") String token,
                              @Value("${telegram.bot.username}") String username,
                              OpenAiService aiService,
                              CustomerRepository customerRepo,
                              AccountOperationRepository operationRepo) {
        super(token);
        this.botUsername = username;
        this.aiService = aiService;
        this.customerRepo = customerRepo;
        this.operationRepo = operationRepo;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        String msg = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();

        Customer user = customerRepo.findByIdTelegram(chatId);
        BotState state = (user == null) ? BotState.NOT_AUTHENTICATED : BotState.AUTHENTICATED;

        switch (state) {
            case NOT_AUTHENTICATED -> handleUnauth(msg, chatId);
            case AUTHENTICATED -> handleAuth(msg, chatId, user);
        }
    }

    private void handleUnauth(String msg, long chatId) {
        if (msg.startsWith("/link")) {
            linkAccount(msg, chatId);
        } else {
            send(chatId, "Bonjour,\nPour relier votre compte, tapez : `/link votre@email.com`");
        }
    }

    private void handleAuth(String msg, long chatId, Customer user) {
        chatWithAI(msg, chatId, user);
    }

    private void linkAccount(String message, Long chatId) {
        try {
            String[] parts = message.split(" ");
            if (parts.length < 2) {
                send(chatId, "Format attendu : `/link email@exemple.com`");
                return;
            }

            String email = parts[1].trim();
            Customer c = customerRepo.findByEmail(email);
            if (c == null) {
                send(chatId, "Aucun compte associé à cet email.");
                return;
            }

            c.setIdTelegram(chatId);
            customerRepo.save(c);

            send(chatId, "Compte associé avec succès : " + c.getName());
        } catch (Exception e) {
            send(chatId, "Erreur lors de l'association du compte.");
            e.printStackTrace();
        }
    }

    private void chatWithAI(String message, Long chatId, Customer user) {
        String context = buildFinancialContext(user);
        String prompt = "Assistant bancaire pour " + user.getName() +
                ". Informations : [" + context + "].\nQuestion : " + message;

        String response = aiService.generateResponse(prompt);
        send(chatId, response);
    }

    private String buildFinancialContext(Customer user) {
        try {
            if (user.getBankAccounts() == null || user.getBankAccounts().isEmpty())
                return "Pas de compte enregistré.";

            StringBuilder sb = new StringBuilder();
            for (BankAccount acc : user.getBankAccounts()) {
                sb.append("Compte ").append(acc.getId())
                        .append(" (").append(acc.getClass().getSimpleName()).append(")")
                        .append(" solde=").append(acc.getBalance()).append(". ");

                List<AccountOperation> ops = operationRepo
                        .findByBankAccountIdOrderByOperationDateDesc(acc.getId());
                if (!ops.isEmpty()) {
                    AccountOperation op = ops.get(0);
                    sb.append("Dernière opération : ").append(op.getType())
                            .append(" ").append(op.getAmount()).append(". ");
                }
            }
            return sb.toString();
        } catch (Exception e) {
            return "Impossible de récupérer les données.";
        }
    }

    @Override
    public void envoyerMessage(long chatId, String text) {
        send(chatId, text);
    }

    private void send(long chatId, String text) {
        SendMessage msg = new SendMessage();
        msg.setChatId(String.valueOf(chatId));
        msg.setText(text);
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }
}
