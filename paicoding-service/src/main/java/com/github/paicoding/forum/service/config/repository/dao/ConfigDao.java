package com.github.paicoding.forum.service.config.repository.dao;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.paicoding.forum.api.model.enums.ConfigTypeEnum;
import com.github.paicoding.forum.api.model.enums.PushStatusEnum;
import com.github.paicoding.forum.api.model.enums.YesOrNoEnum;
import com.github.paicoding.forum.api.model.vo.PageParam;
import com.github.paicoding.forum.api.model.vo.banner.dto.ConfigDTO;
import com.github.paicoding.forum.service.config.converter.ConfigConverter;
import com.github.paicoding.forum.service.config.converter.ConfigStructMapper;
import com.github.paicoding.forum.service.config.repository.entity.ConfigDO;
import com.github.paicoding.forum.service.config.repository.mapper.ConfigMapper;
import com.github.paicoding.forum.service.config.repository.params.SearchConfigParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author YiHui
 * @date 2022/9/2
 */
@Repository
public class ConfigDao extends ServiceImpl<ConfigMapper, ConfigDO> {

    /**
     * 根据类型获取配置列表（无需分页）
     *
     * @param type
     * @return
     */
    public List<ConfigDTO> listConfigByType(Integer type) {
        List<ConfigDO> configDOS = lambdaQuery()
                .eq(ConfigDO::getType, type)
                .eq(ConfigDO::getStatus, PushStatusEnum.ONLINE.getCode())
                .eq(ConfigDO::getDeleted, YesOrNoEnum.NO.getCode())
                .orderByAsc(ConfigDO::getRank)
                .list();
        return ConfigConverter.toDTOS(configDOS);
    }

    private LambdaQueryChainWrapper<ConfigDO> createConfigQuery(SearchConfigParams params) {
        return lambdaQuery()
                .eq(ConfigDO::getDeleted, YesOrNoEnum.NO.getCode())
                .like(StringUtils.isNotBlank(params.getName()), ConfigDO::getName, params.getName())
                .eq(params.getType() != null && params.getType() != -1, ConfigDO::getType, params.getType());
    }

    /**
     * 获取所有 Banner 列表（分页）
     *
     * @return
     */
    public List<ConfigDTO> listBanner(SearchConfigParams params) {
        List<ConfigDO> configDOS = createConfigQuery(params)
                .orderByDesc(ConfigDO::getUpdateTime)
                .orderByAsc(ConfigDO::getRank)
                .last(PageParam.getLimitSql(
                        PageParam.newPageInstance(params.getPageNum(), params.getPageSize())))
                .list();
        return ConfigStructMapper.INSTANCE.toDTOS(configDOS);
    }

    /**
     * 获取所有 Banner 总数（分页）
     *
     * @return
     */
    public Long countConfig(SearchConfigParams params) {
        return createConfigQuery(params)
                .count();
    }

    /**
     * 获取所有公告列表（分页）
     *
     * @return
     */
    public List<ConfigDTO> listNotice(PageParam pageParam) {
        List<ConfigDO> configDOS = lambdaQuery()
                .eq(ConfigDO::getType, ConfigTypeEnum.NOTICE.getCode())
                .eq(ConfigDO::getDeleted, YesOrNoEnum.NO.getCode())
                .orderByDesc(ConfigDO::getCreateTime)
                .last(PageParam.getLimitSql(pageParam))
                .list();
        return ConfigConverter.toDTOS(configDOS);
    }

    /**
     * 获取所有公告总数（分页）
     *
     * @return
     */
    public Integer countNotice() {
        return lambdaQuery()
                .eq(ConfigDO::getType, ConfigTypeEnum.NOTICE.getCode())
                .eq(ConfigDO::getDeleted, YesOrNoEnum.NO.getCode())
                .count()
                .intValue();
    }

    /**
     * 更新阅读相关计数
     */
    public void updatePdfConfigVisitNum(long configId, String extra) {
        lambdaUpdate().set(ConfigDO::getExtra, extra)
                .eq(ConfigDO::getId, configId)
                .update();
    }
}
