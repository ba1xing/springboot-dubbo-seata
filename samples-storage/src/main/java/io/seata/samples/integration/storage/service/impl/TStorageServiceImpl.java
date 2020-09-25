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
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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

    @Resource
    private RedissonClient redissonClient;

    @Override
    public ObjectResponse decreaseStorage(CommodityDTO commodityDTO) {
        ObjectResponse<Object> response = new ObjectResponse<>();
        String key = "STORAGE_LOCK_" + commodityDTO.getCommodityCode();
        RLock lock = redissonClient.getLock(key);
        try {
            //lock.lock(10, TimeUnit.SECONDS);
            boolean isLock = lock.tryLock();
            if (isLock) {
                int storage = baseMapper.decreaseStorage(commodityDTO.getCommodityCode(), commodityDTO.getCount());
                if (storage > 0) {
                    response.setStatus(RspStatusEnum.SUCCESS.getCode());
                    response.setMessage(RspStatusEnum.SUCCESS.getMessage());
                    return response;
                }
            }
        } finally {
            lock.unlock();
        }
        response.setStatus(RspStatusEnum.FAIL.getCode());
        response.setMessage(RspStatusEnum.FAIL.getMessage());
        return response;
    }
}
