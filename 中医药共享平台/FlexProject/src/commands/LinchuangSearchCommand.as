package commands
{
	
	import business.delegates.LinchuangSearchDelegate;
	
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import model.TsfModelLocator;
	
	import mx.rpc.IResponder;
	import mx.controls.Alert;
	
	public class LinchuangSearchCommand implements ICommand, IResponder
	{
		
		[Bindable]
		private var modelLocator:TsfModelLocator = TsfModelLocator.getInstance();
		
		public function execute(event:CairngormEvent):void
		{
			var delegate:LinchuangSearchDelegate = new LinchuangSearchDelegate(this);
			delegate.search();
		}
		
		public function result(data:Object):void
		{
			if (data.result != null)
			{
				if (data.result is XML)
				{
					var result:XML = data.result as XML;
					modelLocator.linChuang.disasterResult = result;
				}
			}
		}
		
		public function fault(info:Object):void
		{
			Alert.show(info.fault.message, "错误");
		}
		
		public function LinchuangSearchCommand()
		{
		}

	}
}