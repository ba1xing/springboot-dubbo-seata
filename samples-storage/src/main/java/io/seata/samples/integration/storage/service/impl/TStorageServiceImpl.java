package io.seata.samples.integration.storage.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import io.seata.samples.integration.common.dto.CommodityDTO;
import io.seata.samples.integration.common.enums.RspStatusEnum;
import io.seata.samples.integration.common.response.ObjectResponse;
import io.seata.samples.integration.storage.entity.TStorage;
import io.seata.samples.integration.storage.mapper.TStorageMapper;
import io.seata.samples.integration.storage.service.ITStorageService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 库存服务实现类
 * </p>
 *
 * @author heshouyou
 * @since 2019-01-13
 */
@Service
public class TStorageServiceImpl extends ServiceImpl<TStorageMapper, TStorage> implements ITStorageService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public ObjectResponse decreaseStorage(CommodityDTO commodityDTO) {
        ObjectResponse<Object> response = new ObjectResponse<>();
        String key = "STORAGE_LOCK_" + commodityDTO.getCommodityCode();
        RLock lock = redissonClient.getLock(key);
        try {
            //lock.lock(10, TimeUnit.SECONDS);
            boolean isLock = lock.tryLock(100, 10, TimeUnit.SECONDS);
            if (isLock) {
                logger.info("加锁成功");
                int storage = baseMapper.decreaseStorage(commodityDTO.getCommodityCode(), commodityDTO.getCount());
                if (storage > 0) {
                    response.setStatus(RspStatusEnum.SUCCESS.getCode());
                    response.setMessage(RspStatusEnum.SUCCESS.getMessage());
                    return response;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
            logger.info("释放锁");
        }
        response.setStatus(RspStatusEnum.FAIL.getCode());
        response.setMessage(RspStatusEnum.FAIL.getMessage());
        return response;
    }
}
