package com.github.snail.verify.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.snail.common.Utils;
import com.github.snail.core.CaptchaController;
import com.github.snail.core.ClientVerifyContext;
import com.github.snail.core.Verify;
import com.github.snail.decoder.TracePositionTraceDecoder;
import com.github.snail.decoder.impl.Base64TracePositionTraceDecoder;
import com.github.snail.graph.TimeTracePosition;
import com.github.snail.json.JSONParser;
import com.github.snail.verify.VerifyResult;

/**
 * 滑动轨迹验证
 */
public class TraceHandler extends BaseVerifyHandler {

	public TraceHandler(CaptchaController captchaController) {
		super(captchaController);
	}

	@Override
	protected VerifyResult doVerify(HttpServletRequest request, HttpServletResponse response,ClientVerifyContext verifyContext) {
		//检测轨迹参数 move trace
		String mtJsongencryptedString = request.getParameter("mt");
		if(Utils.isEmpty(mtJsongencryptedString)) {
			log.warn("verify fail cause mt is empty 0 !!! mybe someone want study me");
			return DefaultVerifyResult.FailResult;
		}
		
		TracePositionTraceDecoder tracePositionTraceDecoder = new Base64TracePositionTraceDecoder();
		
		String mtJsongString = null;
		
		try {
			mtJsongString = tracePositionTraceDecoder.decode(mtJsongencryptedString);			
		}catch (Exception e) {
			log.error("verify fail cause decode string["+ mtJsongencryptedString +"] error,cause:"+e.getMessage(),e);
			return DefaultVerifyResult.FailResult;
		}
		
		JSONParser parser = null;
		try {
			parser = new JSONParser(mtJsongString);
		}catch (IllegalArgumentException e) {
			log.error("verify fail cause param mt has format error !!!,mybe someone want study me,origin string is :"+mtJsongString,e);
			return DefaultVerifyResult.FailResult;
		}
		List<Object> list = parser.parseArray();
		int clientTracePositionCount = list.size();
		
		//轨迹至少是2个
		if(clientTracePositionCount <= 2) {
			log.warn("verify fail cause move trace count is "+ clientTracePositionCount +" !!! mybe some want study me,origin string is :"+mtJsongString);
			return DefaultVerifyResult.FailResult;
		}
		
		//符合配置当中配置的最小轨迹个数和最大轨迹个数要求
		boolean isInRangeTraceCount = this.getCaptchaController().getAllowMinSlideTracePositionCount() <= clientTracePositionCount &&  clientTracePositionCount <= this.getCaptchaController().getAllowMaxSlideTracePositionCount();
		if(!isInRangeTraceCount) {
			log.warn("move trace count is"+ clientTracePositionCount +" and out of ["+ this.getCaptchaController().getAllowMinSlideTracePositionCount() +"," + this.getCaptchaController().getAllowMaxSlideTracePositionCount()+"] range !!! mybe some want study me"+mtJsongString);
			return DefaultVerifyResult.FailResult;
		}
		
		List<TimeTracePosition> traces = new ArrayList<TimeTracePosition>(list.size());
		
		//必须包含正确的数字
		int formatErrorCount = 0;
		for(Object o : list) {
			if(o instanceof Map) {
				
				Map<String,Object> map = (Map)o;
				String x = map.get("x").toString();
				String y = map.get("y").toString();
				String time = map.get("t").toString();
				try {
					traces.add(new TimeTracePosition(Integer.parseInt(x), Integer.parseInt(y), Long.parseLong(time)));					
				}catch (NumberFormatException e) {
					log.warn("verify fail cause param mt has format error !!!,mybe someone want study me,origin string is :"+mtJsongString,e);
					formatErrorCount ++;
					break;
				}
			}else {
				formatErrorCount ++;
				break;
			}
		}
		
		//有错误的格式化数字
		if(formatErrorCount > 0) {
			log.warn("verify fail cause param mt format is not right !!! mybe someone want study me,origin string is :"+mtJsongString);
			return DefaultVerifyResult.FailResult;
		}
		
		//不允许回头你却回头了
		if(!this.getCaptchaController().isAllowSlideRollback() && this.isSlideRollback(traces)) {
			log.debug("verify fail cause slide rollback");
			return DefaultVerifyResult.FailResult;
		}
		
		//所有数字必须是非负数
		if(!this.isAllNumberPositive(traces)) {
			log.warn("verify fail cause some number is positive !!! mybe someone want study me,origin string is :"+mtJsongString);
			return DefaultVerifyResult.FailResult;
		}
		
		//是否全局坐标升序
		if(!this.isAllTraceOrdered(traces)) {
			log.warn("verify fail cause trace is not ordered !!! mybe someone want study me,origin string is :"+mtJsongString);
			return DefaultVerifyResult.FailResult;
		}
		
		//是否全局时间升序
		if(!this.isAllTimeOrdered(traces)) {
			log.warn("verify fail cause time is not ordered !!! mybe someone want study me,origin string is :"+mtJsongString);
			return DefaultVerifyResult.FailResult;
		}
		
		//时间是否比context中的都大
		if(!this.isAllTimeIsOverCreateTimeInContext(traces,verifyContext.getCreateTime().getTime())) {
			log.warn("verify fail cause time is not over createTime in verifyContext !!! mybe someone want study me,origin string is :"+mtJsongString);
			return DefaultVerifyResult.FailResult;
		}
		
		log.debug("move trace count = " + clientTracePositionCount);
		
		TimeTracePosition startMovePosition = traces.get(0);
		TimeTracePosition lastMovePositio = traces.get(traces.size() - 1);
		
		int moveDistance = lastMovePositio.x - startMovePosition.x;
		log.debug("moveDistance = " + moveDistance);
		
		long moveCostTime = lastMovePositio.time - startMovePosition.time;
		log.debug("moveCostTime = " + moveCostTime + " ms");
		
		Verify verify = verifyContext.getVerify();
		
		//图片中的挖去的小图片的x中距离
		int blockDistance = verify.getX();
		
		int minDistance = moveDistance - this.getCaptchaController().getAllowMinMissMathDistance();
		int maxDistance = moveDistance + this.getCaptchaController().getAllowMinMissMathDistance();
		//在最小范围和最大范围之内
		boolean isInRange = minDistance <= blockDistance && blockDistance <= maxDistance;
		
		if(!isInRange) {
			log.debug("verify fail cause moveDistance["+ moveDistance +"] is out of distance["+ minDistance+","+ maxDistance +"] range");
			return DefaultVerifyResult.FailResult;
		}
		
		//消耗时间在范围内
		boolean isInCostTimeRange = this.getCaptchaController().getAllowMinSlideTimeMillisecond() <= moveCostTime && moveCostTime <= this.getCaptchaController().getAllowMaxSlideTimeMillisecond();
		if(!isInCostTimeRange) {
			log.debug("verify fail cause moveCostTime["+ moveCostTime +"] is out of distance["+ this.getCaptchaController().getAllowMinSlideTimeMillisecond()+","+ this.getCaptchaController().getAllowMaxSlideTimeMillisecond() +"] range");
			return DefaultVerifyResult.FailResult;
		}
		return DefaultVerifyResult.SuccessResult;
	}
	
	private boolean isAllTimeIsOverCreateTimeInContext(List<TimeTracePosition> traces, long time) {
		int size = traces.size();
		for(int i=0;i<size - 1;i++) {
			TimeTracePosition p = traces.get(i);
			if(p.time <= time) {
				return false;
			}
		}
		return true;
	}

	private boolean isAllNumberPositive(List<TimeTracePosition> traces) {
		int size = traces.size();
		for(int i=0;i<size - 1;i++) {
			TimeTracePosition p = traces.get(i);
			if(p.x <= 0 || p.y <= 0 || p.time <= 0) {
				return false;
			}
		}
		return true;
	}

	private boolean isSlideRollback(List<TimeTracePosition> traces) {
		//such as [ 1, 2 , 3, 4 , 100 , 10 , 20 ]
		//find max
		TimeTracePosition maxXPosition = traces.get(0);
		int size = traces.size();
		int i = 0;
		for(i=0;i<size - 1;i++) {
			TimeTracePosition p = traces.get(i);
			if(p.x > maxXPosition.x) {
				maxXPosition = p;
			}
		}
		//the last one
		if(i == traces.size() - 1) {
			return false;
		}
		//not last one
		return true;
	}

	private boolean isAllTraceOrdered(List<TimeTracePosition> traces) {
		int size = traces.size();
		for(int i=0;i<size - 2;i++) {
			for(int j = i+1;j< size-1;j++) {
				TimeTracePosition left = traces.get(i);
				TimeTracePosition right = traces.get(j);
				if(left.x > right.x) {
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean isAllTimeOrdered(List<TimeTracePosition> traces) {
		int size = traces.size();
		for(int i=0;i<size - 2;i++) {
			for(int j = i+1;j< size-1;j++) {
				TimeTracePosition left = traces.get(i);
				TimeTracePosition right = traces.get(j);
				if(left.time > right.time) {
					return false;
				}
			}
		}
		return true;
	}
}
