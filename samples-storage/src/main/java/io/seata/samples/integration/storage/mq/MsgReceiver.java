package io.seata.samples.integration.storage.mq;

import com.rabbitmq.client.Channel;
import io.seata.samples.integration.common.dto.CommodityDTO;
import io.seata.samples.integration.common.response.ObjectResponse;
import io.seata.samples.integration.storage.config.ConsumerConfig;
import io.seata.samples.integration.storage.service.ITStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = ConsumerConfig.QUEUE_A)
public class MsgReceiver {
 
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ITStorageService itStorageService;
 
    @RabbitHandler
    public void process(String content) {
        logger.info("接收处理队列A当中的消息： " + content);
    }

    @RabbitHandler
    public void process1(CommodityDTO commodityDTO, Channel channel) {
        logger.info("接收处理队列A当中的消息： " + commodityDTO.toString());
        ObjectResponse response = itStorageService.decreaseStorage(commodityDTO);
        if (response.getStatus() == 200){
            logger.info("库存扣减成功");
//            channel.basicAck;
        }
    }
 
}
