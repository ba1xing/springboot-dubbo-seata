package io.seata.samples.integration.order.controller;

import io.seata.samples.integration.common.dto.CommodityDTO;
import io.seata.samples.integration.common.dto.OrderDTO;
import io.seata.samples.integration.common.response.ObjectResponse;
import io.seata.samples.integration.order.service.ITOrderService;
import io.seata.samples.integration.order.mq.MsgProducer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 订单服务
 * </p>
 *
 * @author heshouyou
 * @since 2019-01-13
 */
@RestController
@RequestMapping("/order")
@Slf4j
public class TOrderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TOrderController.class);


    @Autowired
    private ITOrderService orderService;

    @Autowired
    private MsgProducer msgProducer;

    @PostMapping("/create_order")
    ObjectResponse<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO) {
        LOGGER.info("请求订单微服务：{}", orderDTO.toString());
        return orderService.createOrder(orderDTO);
    }

    @PostMapping("/toNoticeStotage")
    public ObjectResponse toNoticeStotage(String msg) {
        LOGGER.info("消息：{}", msg);
        msgProducer.sendMsg(msg);
        LOGGER.info("消息：{}", msg);
        ObjectResponse objectResponse = new ObjectResponse();
        objectResponse.setData("succeess");
        return objectResponse;
    }

    @PostMapping("/toNoticeStotage1")
    public ObjectResponse toNoticeStotage1(CommodityDTO commodityDTO) {
        LOGGER.info("消息：{}", commodityDTO.toString());
        msgProducer.sendObect(commodityDTO);
        ObjectResponse objectResponse = new ObjectResponse();
        objectResponse.setData("succeess");
        return objectResponse;
    }
}

