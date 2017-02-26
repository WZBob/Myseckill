//存放主要的交互逻辑js代码
//javascript 模块化，避免混乱
//json表示对象的方式
//访问方式；seckill.detail.init(param)
var seckill={
    //封装秒杀相关的ajax的url
    URL :{
        now : function () {
            return '/myseckill/time/now';
        },
        exposer : function (seckillId) {
            return '/myseckill/'+seckillId+'/exposer';
        },
        execution : function (seckillId, md5) {
            return '/myseckill/'+seckillId+'/'+md5+'/execution';
        }
    },
    //处理秒杀逻辑
    handlerSeckillkill : function (seckillId,node) {
        node.hide().html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');
        $.post(seckill.URL.exposer(seckillId),{},function (result) {
            //在回掉函数中执行交互流程
            if(result && result['success']){
                var exposer=result['data'];
                if(exposer['exposed']){
                    //开启秒杀
                    var md5 = exposer['md5'];
                    var killUrl=seckill.URL.execution(seckillId,md5);
                    console.log("killUrl:"+ killUrl);
                    //绑定一次点击事件
                    $('#killBtn').one('click',function () {
                        //执行秒杀请求
                        //1:禁用按钮
                        $(this).addClass('disabled');
                        //2：发送秒杀请求执行秒杀
                        $.post(killUrl,{},function (result) {
                            if(result && result['success']){
                                var killResult=result['data'];
                                var state = killResult['state'];
                                var stateInfo=killResult['stateInfo'];
                                console.log("stateInfo"+killResult['state']+killResult['stateInfo']);
                                //3:显示秒杀结果
                                node.html('<span class="label label-success">'+stateInfo+'</span>');
                            }
                        });
                    });
                    node.show();
                }else{
                    //未开启秒杀，服务器与客户端计时不同
                    var now = exposer['now'];
                    var start = exposer['start'];
                    var end=exposer['end'];
                    seckill.countdown(seckillId,now,start,end);//重新计算计时逻辑
                }
            }else {
                console.log('result:'+result);
            }
        });
    },
    validatePhone : function (phone) {
        if(phone && phone.length==11 && !isNaN(phone)){
            return true;
        }  else {
            return false;
        }
    },
    //判断时间
    countdown : function (seckillId, nowTime, startTime, endTime) {
        var seckillBox = $('#seckill-box');//获取box结点
        if(nowTime > endTime){
            //秒杀结束
            seckillBox.html('秒杀结束！');
        }else if(nowTime < startTime){
            //秒杀为开始，计时事件绑定
            var killTime=new Date(startTime+1000);//未来秒杀时间
            seckillBox.countdown(killTime,function (event) {
                var format=event.strftime('秒杀倒计时： %D天 %H时 %M分 %S秒');
                seckillBox.html(format);
            }).on('finish countdown',function () { //时间完成后回调事件
                //获取秒杀地址，控制显示逻辑，执行秒杀；即出现秒杀按钮
                seckill.handlerSeckillkill(seckillId,seckillBox);
            });
        }else{
            //秒杀一开始
            seckill.handlerSeckillkill(seckillId,seckillBox);
        }
    },
    //详情页秒杀逻辑
    detail :{
        //详情页初始化
        init : function (params) {
            //收集验证和登录  计时交互
            //规划交互流程
            //在cookie中查找手机号
            var killPhone = $.cookie('killPhone');
            //验证手机号，错误时进行提示
            if(!seckill.validatePhone(killPhone)) {
                //绑定手机号
                var killPhoneModal = $('#killPhoneModal');//jQuery选择器，选中该结点
                killPhoneModal.modal({
                    show: true,//显示弹出层
                    backdrop: 'static',//禁止位置关闭，点击其他位置
                    keyboard: false//关闭键盘事件
                });
                $('#killPhoneBtn').click(function () {
                    var inputPhone = $('#killPhoneKey').val();
                    if (seckill.validatePhone(inputPhone)) {
                        $.cookie('killPhone', inputPhone, {expires: 7, path: '/myseckill'});
                        window.location.reload();
                    } else {
                        $('#killPhoneMessage').hide().html('<label class="label label-danger">手机号错误！</label>').show(300);
                    }
                });
            }
            //已经登陆，计时交互
            var startTime= params['startTime'];
            var endTime= params['endTime'];
            var seckillId= params['seckillId'];
            //console.log( $.get(seckill.URL.now(),{},function (result){}));
            $.get(seckill.URL.now(),{},function (result) {
                if(result && result['success']){
                    var nowTime= result['data'];
                    //时间判断,计时交互
                    seckill.countdown(seckillId,nowTime,startTime,endTime);
                }else{
                    console.log('result:'+result);
                }
            });
        }
    }
}