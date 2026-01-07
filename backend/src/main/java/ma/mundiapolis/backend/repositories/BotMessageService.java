package ma.mundiapolis.backend.repositories;


public interface BotMessageService {
    void envoyerMessage(long chatId, String text);
}
