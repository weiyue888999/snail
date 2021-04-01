(function($){
	var DEFAULTS = {
			varsion:'1.0',
			wrapper:'.captcha_wrapper',
			template:"<div class='zfdun_container'><div class='zfdun'><div class='zfdun_captcha_panel'><div class='zfdun_captcha'><div class='zfdun_captcha_bgimg'><img class='zfdun_bgimg_img'><img class='zfdun_bgimg_jigsaw'></div><div class='zfdun_verify_tips' style='display:none;'><p class='zfdun_verify_tips_fail'><span>验证失败</span></p><p class='zfdun_verify_tips_succsss'><span>验证通过</span></p></div></div><div class='zfdun_refresh_btn' style='display:none;'><a></a></div></div><div class='zfdun_captcha_control'><div class='zfdun_slide_indicator' style='display:none;'></div><div class='zfdun_slider_bar'><p>向右拖动滑块填充拼图</p></div></div><div class='zfdun_slider_bar_btn'><span class='zfdun_slider_icon zfdun_slider_icon_normal'></span></div></div></div>",
			imageWidth:338,
			imageHeight:200,
			sliderBarHeight:38,
			sliderBarWidth:40,
			imagePanelCssStyleTop:58,
			requestUrl:'/snail-demo-web/snail',
			verifyTipsFail:'验证失败',
			verifyTipsSuccess:'验证通过',
			verifyTagBar:'向右拖动滑块填充拼图',
			state:'not_verify',
			style:'popup',//popup 弹出式
			popupTitle:'请完成安全验证',
			onVerifySuccess:function(){},
			onVerifyFail:function(){},
	};
	var eventOf = function(e){
		return e = e || window.event;
	};
	var zfdunCaptcha = function(options){
		var initConfig = window.snail_config || {};
		this.options = $.extend({},DEFAULTS,initConfig,options);
		this.render();
		this.init();
		this.afterRender();
		this.initVerifyCallback();
	};
	zfdunCaptcha.prototype = {
		render:function(){
			this.$captchaModal = $(".captcha_modal"); 
			this.$wrapper = $(this.options.wrapper);
			//this.$wrapper.append(this.options.template);
		},
		init : function(){
			
			var $this = this;
			
			//验证码框体相关--start
			this.$captcahModalTitle = this.$captchaModal.find(".captcah_modal_title");
			this.$captcahModalTitle.html(this.options.popupTitle+"<span class='captcah_modal_close'><span class='captcha_icon_close'></span></span>");
			
			this.$captchaModelClose = this.$captchaModal.find(".captcha_icon_close");
			var url = this.options.requestUrl+"?type=resource&instanceId="+ this.options.instanceId +"&name=sprite.png";
			this.$captchaModelClose.css("background-image","url("+url+")");
			
			this.$captchaModelClose.click(function(){
				console.log("closeModal");
				$this.closeModal();
			});
			
			//验证码框体相关--end
			
			this.$wrapper.css("width",this.options.imageWidth);
			
			this.$zfdun_captcha = this.$wrapper.find(".zfdun_captcha");
			
			this.$zfdun_captcha_control = this.$wrapper.find(".zfdun_captcha_control");
			
			this.$slider_bar = this.$wrapper.find(".zfdun_slider_bar");
			
			this.$slider_bar_p = this.$wrapper.find(".zfdun_slider_bar p");
			this.$slider_bar_p.html(this.options.verifyTagBar)
	
			this.$slider_bar_btn = this.$wrapper.find(".zfdun_slider_bar_btn");
			
			this.$refresh_btn = this.$wrapper.find(".zfdun_refresh_btn");
			this.$refresh_btn_a = this.$wrapper.find(".zfdun_refresh_btn a");
			this.$captcha_panel = this.$wrapper.find(".zfdun_captcha_panel");
			
			var height = this.options.imageHeight+5;
			this.$captcha_panel.css("height",height+"px");
			
			this.$bg_img_img = this.$wrapper.find(".zfdun_bgimg_img");
			this.$bgimg_jigsaw = this.$wrapper.find(".zfdun_bgimg_jigsaw");
			this.$zfdun_verify_tips = this.$wrapper.find(".zfdun_verify_tips");
			
			this.$slider_icon = this.$wrapper.find(".zfdun_slider_icon");
			this.$slider_icon_a = this.$wrapper.find(".zfdun_slider_icon a");
			
			//调整滑块的展示背景图片
			var url = this.options.requestUrl+"?type=resource&instanceId="+ this.options.instanceId +"&name=sprite.png";
			this.$slider_icon.css("background-image","url("+url+")");
			this.$refresh_btn_a.css("background-image","url("+url+")");
			
		},
		initVerifyCallback:function(){
			if(typeof this.options.onVerifySuccess === "function"){
				this.onVerifySuccess = this.options.onVerifySuccess;
			}else{
				this.onVerifySuccess = function(){};
			}
			if(typeof this.options.onVerifyFail === "function"){
				this.onVerifyFail = this.options.onVerifyFail;
			}else{
				this.onVerifyFail = function(){};
			}
		},
		afterRender:function(){
			if(this.options.state === "not_verify"){
				if(this.options.style === "popup"){

				}
			}else if(this.options.state === "verified"){
				
			}else{
				//not define
			}
		},
		hideAll:function(){
			this.$wrapper.hide();
		},
		refreshPanelImage:function(){
			
			var $this = this;
			var rtk = $this.options.rtk;
			var t = new Date().getTime();
			var url = $this.options.requestUrl;
			var instanceId = $this.options.instanceId;
			$.ajax({
				async:false,
				type : "GET",
				url : url,
				data:{
					type:"refresh",
					rtk:rtk,
					time:t,
					instanceId:instanceId,
				},
				success : function(result) {
					var si = url+"?type=image&id="+result.si+"&imtk="+result.imtk+"&t="+result.t+"&instanceId="+instanceId;
					var mi = url+"?type=image&id="+result.mi+"&imtk="+result.imtk+"&t="+result.t+"&instanceId="+instanceId;
					
					$this.$bg_img_img.attr("src",si)
					$this.$bgimg_jigsaw.attr("src",mi)						
				},
				error : function(e){
					console.log("refresh error:"+e);
				}
			});
		},
		showRefreshPanelImageBtn:function(){//显示panel上的刷新图片按钮
			this.$refresh_btn.show();
		},
		hideRefreshPanelImageBtn:function(){//隐藏panel上的刷新图片按钮
			this.$refresh_btn.hide();
		},
		initImagePanel:function(refresh){//初始化背景图片
			this.refreshPanelImage();
			this.initMoveListeners();
		},
		initMoveListeners : function(){
			
			var $this = this;
			
			var context = {
					mt:[],
					cleanMt:function(){
						context.mt = [];
					},
					pushMt:function(e){
						context.mt.push({x:e.clientX,y:e.clientY,t:new Date().getTime()});
					}
			};
			var state = 'unmoving';
			var moveStartP = null;
			
			var zfdun_slider_bar_width = $this.$zfdun_captcha_control.width();
			var slider_bar_btn_width = $this.$slider_bar_btn.width();
			var maxMoveDistance = zfdun_slider_bar_width - slider_bar_btn_width;
			console.log("maxMoveDistance"+maxMoveDistance)
			
			var onStartMove = function(e){//开始移动
				
				console.log("onStartMove")
				
				var e = eventOf(e);
				$this.switchMovingStatus();
				state = "moving";
				moveStartP = e.pageX || e.originalEvent.targetTouches[0].pageX;
				context.pushMt(e);
				
				$(window).off('mouseup touchend').on('mouseup touchend', onMoveEnd);
			};
			
			var onMoveing = function(e){//移动中
				if(state != "moving"){
					return;
				}
				
				console.log("moving")
				
				var e = eventOf(e);
				
				var moveX = e.pageX || e.originalEvent.targetTouches[0].pageX;
				var movedDistance = moveX - moveStartP;
				
				if(moveStartP && movedDistance >= 0 && movedDistance <= maxMoveDistance){
					
					$this.movePanelToLeft(movedDistance);
					
					context.pushMt(e);
				}
			};
			
			var onMoveEnd = function(e){//移动结束
				if(state != "moving"){
					return;
				}
				var e = eventOf(e);
				
				console.log("move end")
				
				$this.switchNormalStatus();
				
				state = "unmoving";
				
				context.pushMt(e);
				
				$this.verify(context.mt);
				
				context.cleanMt();
				
				$this.stopMoveListeners();
			};
			
			//鼠标按下 / 手指按下
			this.$slider_bar_btn.off('mousedown touchstart').on('mousedown touchstart', onStartMove);
			//鼠标拖动 / 手指滑动
			$(window).off('mousemove touchmove').on('mousemove touchmove',onMoveing);
		},
		stopMoveListeners : function(){
			//鼠标按下 / 手指按下
			this.$slider_bar_btn.off('mousedown touchstart');
			//鼠标拖动 / 手指滑动
			$(window).off('mousemove touchmove');
			//鼠标结束按下/手指结束滑动
			$(window).off('mouseup touchend');
		},
		initEvent:function(){
			var $this = this;
			this.$refresh_btn_a.off('click').on('click',function(){
				$this.refreshPanelImage();
			});
		},
		resetEvent:function(){
			this.stopEvent();
			this.initEvent();
		},
		stopEvent:function(){
			this.$zfdun_captcha_control.off('mouseenter');
			this.$slider_bar_btn.off('mouseenter');
		},
		switchMovingStatus:function(){
			// 鼠标在滑块按下切换滑块背景
			this.$slider_icon.removeClass("zfdun_slider_icon_normal").addClass("zfdun_slider_icon_moving");
			this.$slider_bar_p.html("");
		},
		switchNormalStatus:function(){
			this.$slider_icon.removeClass("zfdun_slider_icon_moving").addClass("zfdun_slider_icon_normal");
		},
		movePanelToLeft:function(movedDistance){
			console.log("moving..."+movedDistance)
			this.$slider_bar_btn.css("left",movedDistance+"px");
			this.$bgimg_jigsaw.css("left",movedDistance+"px");
		},
		resetPanel:function(){
			
			var $this = this;
			
			var callback = function(){
				
				$this.$slider_icon.removeClass("zfdun_slider_icon_fail").addClass("zfdun_slider_icon_normal");
				$this.$slider_bar_btn.removeClass("zfdun_slider_bar_btn_fail");
				
				$this.$slider_bar_p.html($this.options.verifyTagBar);
				
				$this.initImagePanel(true);
			}
			
			this.$slider_bar_btn.animate({left: '0px'}, "slow",'linear',callback);
			this.$bgimg_jigsaw.animate({left: '0px'}, "slow",'linear');
			
		},
		changeStatusVerifySuccess : function(){
			
			this.options.state = "verified";
			
			this.$slider_icon.removeClass("zfdun_slider_icon_normal").addClass("zfdun_slider_icon_success");
			this.$slider_bar_btn.css("background-color","#52CCBA");
			
			this.hideRefreshPanelImageBtn();
			
			this.$slider_bar_btn.animate({left: '0px'}, "slow");
			this.$bgimg_jigsaw.animate({left: '0px'}, "slow");
			
			this.$slider_bar_p.html(this.options.verifyTipsSuccess);
			
			this.stopEvent();
		},
		changeStatusVerifyFail : function(){
			
			var $this = this;
			
			this.$slider_icon.removeClass("zfdun_slider_icon_normal").addClass("zfdun_slider_icon_fail");
			this.$slider_bar_btn.addClass("zfdun_slider_bar_btn_fail");
			
			setTimeout(function() {
				//回头
				$this.resetPanel();
				
			}, 300);
		},
		verify:function(mt){
			
			var success = this.requestVerifyRemote(mt);
			if(success){
				this.changeStatusVerifySuccess();
				this.notify();
				this.closeModal();
				if(this.onVerifySuccess){
					this.onVerifySuccess();
				}
			}else{
				this.changeStatusVerifyFail();
				if(this.onVerifyFail){
					this.onVerifyFail();
				}
			}
		},
		notify:function(){
			var requestUrl = this.options.requestUrl;
			var instanceId = this.options.instanceId;
			$.ajax({
				async:false,
				type : "POST",
				url : requestUrl,
				data:{
					type:"notify",
					rtk:this.options.rtk,
					time:new Date().getTime(),
					instanceId:instanceId,
				},
				success : function(result) {
					var success = result.status === "success";
				},
				error : function(e){
					console.log(e);
				}
			});
		},
		requestVerifyRemote : function(mt){
			var requestUrl = this.options.requestUrl;
			var instanceId = this.options.instanceId;
			var verifyResult = false;
			var mt = JSON.stringify(mt);
			var mtt = ef(mt);
			$.ajax({
				async:false,
				type : "POST",
				url : requestUrl,
				data:{
					type:"verify",
					rtk:this.options.rtk,
					time:new Date().getTime(),
					mt:mtt,
					instanceId:instanceId,
				},
				success : function(result) {
					verifyResult = result.status === "success";
				},
				error : function(e){
					console.log(e);
				}
			});
			return verifyResult;
		},
		popup:function(){
			if(this.options.state === "not_verify"){
				this.$captchaModal.show();
				this.initImagePanel();
				this.showRefreshPanelImageBtn();
				this.initEvent();				
			}else if(this.options.state === "verified"){
				if(this.onVerifySuccess){
					this.onVerifySuccess();
				}
			}else{
				//undefind state
			}
		},
		closeModal:function(){
			this.$captchaModal.hide();
		},
	};
	window.zfdunCaptcha = zfdunCaptcha;
})(window.zfJQuery);