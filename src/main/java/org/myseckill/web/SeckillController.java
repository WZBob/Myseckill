package org.myseckill.web;

import enums.SeckillStateEnum;
import org.myseckill.dto.Exposer;
import org.myseckill.dto.SeckillExcution;
import org.myseckill.dto.SeckillResult;
import org.myseckill.entity.Seckill;
import org.myseckill.exception.RepeatKillException;
import org.myseckill.exception.SeckillClosedException;
import org.myseckill.exception.SeckillException;
import org.myseckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * MVC控制
 * Created by Zhongbo on 2017/2/20.
 */
@Controller
@RequestMapping("/myseckill")//url:/模块/资源/{id}/细分  如seckill/list
public class SeckillController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    /**
     * 通过GET方式获取列表页
     * @param model
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model){
        List<Seckill> list = seckillService.getSeckillList();
        model.addAttribute("list",list);//list.jsp + model=ModelAndView
        return "list";//WEB-INF/jsp/"list".jsp
    }

    /**
     * 获取详情页,注意URL的设计
     * @param seckillId
     * @param model
     * @return
     */
    @RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId, Model model){
        if (seckillId == null){
            return "redirect:/myseckill/list";//重定向
        }
        Seckill seckill= seckillService.getById(seckillId);
        if (seckill == null){
            return "forward:/myseckill/list";//请求转发
        }
        model.addAttribute("seckill",seckill);
        return "detail";
    }

    //ajax接口 json；方法返回数值，与上边有区别
    @RequestMapping(value = "/{seckillId}/exposer",
            method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId){
        SeckillResult<Exposer> result;
        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<Exposer>(true,exposer);
        } catch (Exception e) {
            logger.error(e.getMessage());
            result = new SeckillResult<Exposer>(false,e.getMessage());
        }
        return result;
    }

    /**
     * 执行秒杀  --->    修改为存储过程执行秒杀
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     */
    @RequestMapping(value = "/{seckillId}/{md5}/execution",
            method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public SeckillResult<SeckillExcution> execute(@PathVariable("seckillId") Long seckillId,
                                                  @CookieValue(value = "killPhone",required = false) Long userPhone,//名称与js缓存一致
                                                  @PathVariable("md5") String md5){
        if(userPhone == null){
            return new SeckillResult<SeckillExcution>(false,"未注册");
        }
        //执行秒杀
        SeckillResult<SeckillExcution> result;
        try{
            SeckillExcution seckillExcution = seckillService.executeSeckillProcedure(seckillId,userPhone,md5);//修改为存储过程
            return new SeckillResult<SeckillExcution>(true,seckillExcution);
        }catch (RepeatKillException e){
            SeckillExcution seckillExcution = new SeckillExcution(seckillId,SeckillStateEnum.REPEAT_KILL);
            return new SeckillResult<SeckillExcution>(true, seckillExcution);
        }catch (SeckillClosedException e){
            SeckillExcution seckillExcution = new SeckillExcution(seckillId,SeckillStateEnum.END);
            return new SeckillResult<SeckillExcution>(true, seckillExcution);
        }catch (SeckillException e){
            SeckillExcution seckillExcution = new SeckillExcution(seckillId,SeckillStateEnum.DATA_REWRITE);
            return new SeckillResult<SeckillExcution>(true, seckillExcution);
        }catch (Exception e){
            SeckillExcution seckillExcution = new SeckillExcution(seckillId,SeckillStateEnum.INNER_ERROR);
            return new SeckillResult<SeckillExcution>(true, seckillExcution);
        }
    }

    /**
     * 获取系统时间
     * @return
     */
    @RequestMapping(value = "/time/now", method = RequestMethod.GET,produces = "application/json;charset=UTF-8")
    @ResponseBody
    public SeckillResult<Long> time(){
        Date now=new Date();
        return new SeckillResult<Long>(true,now.getTime());
    }
// 返回值为String时可以，Long就不行
//    @RequestMapping(value = "/test", method = RequestMethod.GET)
//    @ResponseBody
//    public Long test(){
//        Date now=new Date();
//        return now.getTime();
//    }
}
