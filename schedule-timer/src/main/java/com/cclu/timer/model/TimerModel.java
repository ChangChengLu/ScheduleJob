package com.cclu.timer.model;

import com.cclu.api.dto.timer.NotifyHttpParam;
import com.cclu.api.dto.timer.TimerDTO;
import com.cclu.common.model.BaseModel;
import com.cclu.timer.utils.JSONUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * @author ChangCheng Lu
 * @description 定时器
 */
@Getter
@Setter
public class TimerModel extends BaseModel implements Serializable {

    /**
     * 定时器ID
     */
    private Long timerId;

    /**
     * 业务/应用标识
     */
    private String app;

    private String name;

    /**
     * 定时器状态：
     *  0-新建
     *  1-激活
     *  2-未激活
     */
    private int status;

    private String cron;

    /**
     * 回调上下文
     */
    private String notifyHttpParam;

    /**
     * 包装类转对象
     *
     * @param timerDTO timerDTO
     * @return         TimerModel
     */
    public static TimerModel voToObj(TimerDTO timerDTO) {
        if (timerDTO == null) {
            return null;
        }
        TimerModel timerModel = new TimerModel();
        timerModel.setApp(timerDTO.getApp());
        timerModel.setTimerId(timerDTO.getTimerId());
        timerModel.setName(timerDTO.getName());
        timerModel.setStatus(timerDTO.getStatus());
        timerModel.setCron(timerDTO.getCron());
        timerModel.setNotifyHttpParam(JSONUtil.toJsonString(timerDTO.getNotifyHttpParam()));
        return timerModel;
    }

    /**
     * 对象转包装类
     *
     * @param timerModel timerModel
     * @return           TimerDTO
     */
    public static TimerDTO objToVo(TimerModel timerModel) {
        if (timerModel == null) {
            return null;
        }
        TimerDTO timerDTO = new TimerDTO();
        timerDTO.setApp(timerModel.getApp());
        timerDTO.setTimerId(timerModel.getTimerId());
        timerDTO.setName(timerModel.getName());
        timerDTO.setStatus(timerModel.getStatus());
        timerDTO.setCron(timerModel.getCron());

        NotifyHttpParam httpParam = JSONUtil.parseObject(timerModel.getNotifyHttpParam(), NotifyHttpParam.class);
        timerDTO.setNotifyHttpParam(httpParam);

        BeanUtils.copyProperties(timerModel, timerDTO);
        return timerDTO;
    }

}
