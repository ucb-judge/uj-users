package ucb.judge.ujusers.producer


import org.slf4j.LoggerFactory
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ucb.judge.ujusers.dto.NotificationDto

@Service
class NotificationProducer @Autowired constructor(private val amqpTemplate: AmqpTemplate){
    companion object{
        private val logger = LoggerFactory.getLogger(NotificationProducer::class.java.name)
    }

    fun sendNotification(notificationDto: NotificationDto){
        logger.info("Sending notification")
        amqpTemplate.convertAndSend("notification2Exchange", "notification2RoutingKey", notificationDto)
        logger.info("Message sent")
    }

}