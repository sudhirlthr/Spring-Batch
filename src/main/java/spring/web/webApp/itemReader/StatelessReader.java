package spring.web.webApp.itemReader;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.util.Iterator;

public class StatelessReader implements ItemReader<String> {

    public final Iterator<String> dataIterator;

    public StatelessReader(Iterator<String> dataIterator) {
        this.dataIterator = dataIterator;
    }

    @Override
    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (dataIterator.hasNext()) return dataIterator.next();
        else return null;
    }
}
