//存放主要交互逻辑代码
//javascript 模块化
var seckill={
    //封装秒杀相关的ajax
    URL :{
        now : function(){
            return '/seckill/seckill/time/now';
        },
        exposer : function(seckillId){
            return '/seckill/'+seckillId+'/exposer';
        },
        execution : function(seckillId,md5){
            return '/seckill/seckill/'+seckillId+ '/' +md5+'/execution';
        }
    },
    handleSeckillkill : function(seckillId,node){
        //处理秒杀逻辑
        node.hide().html('<button class = "btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');
        var urll =seckill.URL.exposer(seckillId);
        console.log('urll:'+urll);
        $.post(urll,{'seckillId':seckillId},function(result){
            console.log('resultStart:'+result);
            //在回调函数中执行交互逻辑
            if(result&&result['success']){
                var exposer =result['data'];
                if(exposer['exposed']){
                    //开启秒杀
                    //获取秒杀地址
                    var md5 =exposer['md5'];
                    var killUrl=seckill.URL.execution(seckillId,md5);
                    console.log('killUrl:'+killUrl);
                    //不用click用one 只绑定一次点击事件
                    $('#killBtn').one('click',function(){
                        //执行秒杀请求的操作
                        //先禁用按钮
                        $(this).addClass('disabled');
                        //发送秒杀请求
                        console.log('killUrl:'+killUrl);
                        $.post(killUrl,{},function(result){
                            if(result && result['success']){
                                var killResult=result['data'];
                                var state =killResult['state'];
                                var stateInfo=killResult['stateInfo'];
                                //显示秒杀结果
                                node.html('<span class="label label-success">'+stateInfo+'</span>');
                            }
                        });
                    });
                    node.show();
                }else{
                    //未开启秒杀
                    var now =exposer['now'];
                    var start =exposer['start'];
                    var end =exposer['end'];
                    seckill.countDown(seckillId,now,start,end);
                }
            }else{
                console.log('result:'+result);
            }
        });
    },
    validatePhone:function(phone){
        if(phone&& phone.length == 11&& !isNaN(phone)){
            return true;
        }else{
            return false;
        }
    },
    countDown:function(seckillId,nowTime,startTime,endTime){
        var seckillBox = $('#seckill-box');
        if(nowTime>endTime){
            //秒杀结束
            seckillBox.html('秒杀结束！');
        }else if(nowTime<startTime){
            //秒杀未开始
            var killTime=new Date(startTime+1000);
            seckillBox.countdown(killTime,function(event){
                //时间格式
                var format =event.strftime('秒杀倒计时:%D天 %H时 %M分 %S秒');
                seckillBox.html(format);
                //时间完成后回调事件
            }).on('finish.countdown',function(){
                //获取秒杀地址，控制现实逻辑，执行秒杀
                seckill.handleSeckillkill(seckillId,seckillBox);
            });
        }else{
            //秒杀开始
            seckillBox.html('秒杀开始');
            seckill.handleSeckillkill(seckillId,seckillBox);
        }
    },
    //详情页秒杀逻辑
    detail:{
        //详情页初始化
        init : function(params){
            //手机验证和登录，计时交互
            //规划我们的交互逻辑
            //在cookie中查找手机号
            var killPhone =$.cookie('killPhone');
            var seckillId=params['seckillId'];
            var startTime=params['startTime'];//毫秒
            var endTime=params['endTime'];
            //验证手机号
            if(!seckill.validatePhone(killPhone)){
                //绑定手机号
                var killPhoneModal= $('#killPhoneModal');
                //显示了弹出层
                killPhoneModal.modal({
                    show:true,
                    backdrop:'static',//禁止位置关闭
                    keyboard:false //关闭键盘事件
                });
                $('#killPhoneBtn').click(function(){
                    var inputPhone = $('#killPhoneKey').val();
                    console.log("inputPhone: " + inputPhone);
                    if(seckill.validatePhone(inputPhone)){
                        //电话写入cookie
                        $.cookie('killPhone',inputPhone,{expires:7,path:'/seckill'});
                        //刷新页面
                        window.location.reload();
                    }else{
                        $('#killPhoneMessage').hide().html('<label class="label label-danger">手机号错误!</label>').show(300);

                    }
                });
            }
            //已经登录
            //计时交互
            $.get(seckill.URL.now(),{},function(result){
                if(result&& result['success']){
                    var nowTime =result['data'];
                    //时间判断，计时交互
                    seckill.countDown(seckillId, nowTime, startTime, endTime);
                }else{
                    console.log('result:'+result);
                }
            })
        }
    }

}