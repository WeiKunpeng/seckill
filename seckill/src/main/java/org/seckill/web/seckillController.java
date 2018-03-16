package org.seckill.web;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.Seckill;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/*
 * weikunpeng
 * 2018/3/11 14:36
 */



@Controller
@RequestMapping("/seckill")//url:/模块/资源/{id}/细分/seckill/list;
public class seckillController {
    private Logger logger=(Logger) LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SeckillService seckillService;


    @RequestMapping(value = "/test",method =RequestMethod.POST)
    public String test(@RequestParam("seckillId")String seckillId){
        return seckillId;
    }


    @RequestMapping(value = "/list",method =RequestMethod.GET)
    public String list(Model model){
        System.out.println("调整");
        List<Seckill> list = seckillService.getSeckillList();
        model.addAttribute("list",list);
        //获取列表页
        //list.jsp + model =modelAndView
        return "list";
    }
    @RequestMapping(value = "/{seckillId}/detail",method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId")Long seckillId,Model model){
        if(seckillId==null){
            return "redirect:/seckill/list";
        }
        Seckill seckill = seckillService.getById(seckillId);
        if(seckill==null){
            return "forward:seckill/list";
        }
        model.addAttribute("seckill",seckill);
        return "detail";
    }
    //ajax json
    @RequestMapping(value = "/exposer",
            method = RequestMethod.POST
            /*,produces = {"application/json;charset=UTF-8"}*/)
    @ResponseBody
    public SeckillResult<Exposer> exposer(/*@PathVariable("seckillId")*/@RequestParam("seckillId") Long seckillId){
        SeckillResult<Exposer> result;
        System.out.println("进入");
        try {
            System.out.println("seckillId:"+seckillId);
            Exposer exposer =seckillService.exportSeckillUrl(seckillId);
            result=new SeckillResult<Exposer>(true,exposer);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            result= new SeckillResult<Exposer>(false,e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/{seckillId}/{md5}/execution",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> excute(@PathVariable("seckillId")Long seckillId,
                                                  @PathVariable("md5")String md5,
                                                  @CookieValue(value = "killPhone",required = false)Long userPhone){
        SeckillResult<SeckillExecution> result;
        System.out.println("execution123");
        if(userPhone == null){
            result= new SeckillResult<SeckillExecution>(false,"未注册");
        }

        try {
            //SeckillExecution excution= seckillService.excuteSeckill(seckillId, userPhone, md5);
            //通过存储过程
            SeckillExecution excution= seckillService.executeSeckill(seckillId, userPhone, md5);
            return new SeckillResult(true,excution);
        }catch (RepeatKillException     e1)
        {
            SeckillExecution execution=new SeckillExecution(seckillId, SeckillStatEnum.REPEAT_KILL);
            return new SeckillResult<SeckillExecution>(true,execution);
        }catch (SeckillCloseException e2)
        {
            SeckillExecution execution=new SeckillExecution(seckillId, SeckillStatEnum.END);
            return new SeckillResult<SeckillExecution>(true,execution);
        }
        catch (Exception e)
        {
            SeckillExecution execution=new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
            return new SeckillResult<SeckillExecution>(true,execution);
        }
    }

    //获取系统时间 http://localhost:8080/seckill/seckill/time/now
    @RequestMapping(value = "/time/now",method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time()
    {
        Date now=new Date();
        return new SeckillResult<Long>(true,now.getTime());
    }
}
