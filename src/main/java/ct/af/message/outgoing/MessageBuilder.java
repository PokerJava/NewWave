package ct.af.message.outgoing;

import ec02.data.interfaces.IMessageBuilder;
import ec02.exception.BuilderParserException;

public class MessageBuilder implements IMessageBuilder
{
    public MessageBuilder(String message)
    {
        this.message = message;
    }

    private String message;

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    @Override
    public String buildMessage() throws BuilderParserException
    {
        return message;
    }
}
