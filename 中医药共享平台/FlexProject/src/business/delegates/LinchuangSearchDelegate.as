package business.delegates
{
	import com.adobe.cairngorm.business.ServiceLocator;
	
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	import mx.rpc.http.HTTPService;
	import mx.controls.Alert;
	
	public class LinchuangSearchDelegate
	{
		
		private var serviceLocator:ServiceLocator = ServiceLocator.getInstance();
		private var _service:HTTPService;
		private var _responder:IResponder;
		
		public function LinchuangSearchDelegate(responder:IResponder)
		{
			_service = serviceLocator.getHTTPService("linchuangSearchService");
			_responder = responder;
		}
		
		public function search():void
		{
			var key:String = "大黄";
			var source:String = "症状";
			var param:Object = {key: key, source: source};
			param.index = 1;
			var token:AsyncToken = _service.send(param);
			token.addResponder(_responder);
		}

	}
}