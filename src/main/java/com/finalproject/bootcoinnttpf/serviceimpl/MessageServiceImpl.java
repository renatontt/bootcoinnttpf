package com.finalproject.bootcoinnttpf.serviceimpl;

import com.finalproject.bootcoinnttpf.dto.Result;
import com.finalproject.bootcoinnttpf.dto.TransactionEvent;
import com.finalproject.bootcoinnttpf.model.Account;
import com.finalproject.bootcoinnttpf.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MessageServiceImpl {

    @Autowired
    private StreamBridge streamBridge;

    public boolean sendResult(Result result){
        return streamBridge.send("result-out-0",result);
    }

    public void sendAcceptMessage(Transaction transaction){
        String message = "Your purchase request has been accepted, with the next transaction id:"+
                transaction.getTransactionId() + " the amount is: PEN" + transaction.getAmountFx() +
                " the expiration time of the transaction is: "+transaction.getExpiration();
        streamBridge.send("result-out-0",new Result(transaction.getFrom(),"Pending of payment",message));
    }

    public void sendToTransaction(TransactionEvent transactionEvent, Account account){

        transactionEvent.setUserId(account.getId());
        transactionEvent.setTypeAccount(account.getPayment());
        transactionEvent.setState(account.getPayment());

        String number = account.getPayment().equalsIgnoreCase("yanki")?
                account.getPhone().toString():
                account.getAccount();

        transactionEvent.setNumber(number);

        streamBridge.send("transaction-out-0",transactionEvent);
    }

}
