package org.hung.solace;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.solacesystems.jcsmp.BytesMessage;
import com.solacesystems.jcsmp.DeliveryMode;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.JCSMPSession;
import com.solacesystems.jcsmp.JCSMPStreamingPublishEventHandler;
import com.solacesystems.jcsmp.SpringJCSMPFactory;
import com.solacesystems.jcsmp.TextMessage;
import com.solacesystems.jcsmp.Topic;
import com.solacesystems.jcsmp.XMLMessageProducer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SolaceClient {

	@Autowired
	private SpringJCSMPFactory solaceFactory;
	
	private JCSMPSession session;
	
	private XMLMessageProducer producer;
	
	@PostConstruct
	public void init() throws JCSMPException {
		log.info("Init SolaceClient bean...");
		session = solaceFactory.createSession();
		producer = session.getMessageProducer(new JCSMPStreamingPublishEventHandler() {
			
			@Override
			public void responseReceived(String arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void handleError(String arg0, JCSMPException arg1, long arg2) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	@PreDestroy
	public void destory() {
		if (producer!=null) {
			producer.close();
		}
		if (session!=null) {
			session.closeSession();
		}
		log.info("Destory SolaceClient bean...");
	}	
	
	public void sendTextSMFMessage(Topic topic, String text) throws JCSMPException {
		TextMessage msg = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
		msg.setText(text);
		msg.setDeliveryMode(DeliveryMode.PERSISTENT);
		msg.setAckImmediately(true);
		producer.send(msg, topic);
		log.info("message sent"+msg.dump());		
	}

	public void sendBytesSMFMessage(Topic topic, byte[] data) throws JCSMPException {
		BytesMessage msg = JCSMPFactory.onlyInstance().createMessage(BytesMessage.class);
		msg.setData(data);
		msg.setDeliveryMode(DeliveryMode.PERSISTENT);
		msg.setAckImmediately(true);
		producer.send(msg, topic);
		log.info("message sent"+msg.dump());	
	}
	
	
}
