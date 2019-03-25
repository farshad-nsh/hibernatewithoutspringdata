package com.farshad;



import com.farshad.domain.ETF;
import com.farshad.domain.commands.SellShareCommand;
import com.farshad.domain.Share;
import com.farshad.domain.commands.BuyShareCommand;
import com.farshad.infrastructure.commanddispatcher.AbstractCommandListener;
import com.farshad.infrastructure.commanddispatcher.CommandDispatcher;
import com.farshad.infrastructure.commanddispatcher.Invoker;
import com.farshad.infrastructure.eventdispatcher.MessageDispatcherImpl;
import com.farshad.infrastructure.listener.MessageListenerFactory;
import com.farshad.infrastructure.sqlRepository.Repository;
import com.google.protobuf.Message;
import com.farshad.infrastructure.protos.generated.domain.BondProtos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transaction;
import javax.transaction.Transactional;
import java.util.List;

@SpringBootApplication
public class App {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    MessageListenerFactory messageListenerFactory;

    @Autowired
    Repository repository;




    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    public CommandLineRunner demo() {
        return (args) -> {
            System.out.println("reflection once only!");
            BondProtos.Bond.Builder asset1 = BondProtos.Bond.newBuilder().setName("farshadBond").setType("asset").setValue(5);
            Message builtMessage1=asset1.build();

            BondProtos.Bond.Builder asset2 = BondProtos.Bond.newBuilder().setName("betaBond").setType("liability").setValue(2);
            Message builtMessage2=asset2.build();


            MessageDispatcherImpl messageDispatcher=applicationContext.getBean(MessageDispatcherImpl.class);
            messageDispatcher.dispatchEvent("assets", builtMessage1);
            messageDispatcher.dispatchEvent("liabilities", builtMessage2);

            CommandDispatcher commandDispatcher=applicationContext.getBean(CommandDispatcher.class);
            commandDispatcher.dispatchCommand(builtMessage1);


            ETF etf=new ETF();

            etf.setName("IranianBond10");
            etf.setType("ETF");
            etf.setValue(11.124);
            repository.save(etf);

            List<ETF> etfList=repository.readETFByTypeUsingNativeQuery("ETF");
            for (ETF  e:etfList
                 ) {
                System.out.println("e="+e.getValue());
            }
        };
    }

}
