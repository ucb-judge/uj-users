package ucb.judge.ujusers.config

import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMqConfig {

    @Bean
    fun notificationExchange(): DirectExchange {
        return DirectExchange("notification2Exchange")
    }

    @Bean
    fun notificationQueue(): Queue {
        return QueueBuilder.durable("notification2Queue").build()
    }

    @Bean
    fun notificationBinding(): Binding {
        return BindingBuilder
            .bind(notificationQueue())
            .to(notificationExchange())
            .with("notification2RoutingKey")
    }

    @Bean
    fun converter(): Jackson2JsonMessageConverter{
        return Jackson2JsonMessageConverter()
    }

    @Bean
    fun amqpTemplate(connectionFactory: ConnectionFactory): AmqpTemplate {
        val rabbitTemplate = RabbitTemplate(connectionFactory)
        rabbitTemplate.messageConverter = converter()
        return rabbitTemplate
    }
}