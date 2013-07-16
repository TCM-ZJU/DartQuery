package commands
{
	
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import business.delegates.LinChuangGeneralSearchDelegate;
	import events.LinChuangGeneralSearchEvent;
	
	import flexlib.containers.SuperTabNavigator;
	import model.TsfModelLocator;
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.core.Application;
	import view.LinchuangGeneralInfoView;
	
	public class LinChuangGeneralSearchCommand implements ICommand, IResponder
	{
		[Bindable]
		private var modelLocator:TsfModelLocator = TsfModelLocator.getInstance();
		
		public var key:String;
		public var source:String;
		public var related:String;
		public var searchType:int;
		
		public function execute(event:CairngormEvent):void
		{
			var generalSearchEvent:LinChuangGeneralSearchEvent = event as LinChuangGeneralSearchEvent;
			key = generalSearchEvent.key;
			source = generalSearchEvent.source;
			related = generalSearchEvent.related;
			searchType = modelLocator.searchType;
			var delegate:LinChuangGeneralSearchDelegate = new LinChuangGeneralSearchDelegate(this);
			delegate.search(key, source, related, searchType);
		//	Application.application.enabled = false;
		}
		
		public function result(data:Object):void
		{
			
		//	Application.application.enabled = true;
			if (data.result != null)
			{
				if (data.result is XML)
				{		
					var tabNavigate:SuperTabNavigator = modelLocator.resultView.tabNav;
					var displayView:Object = tabNavigate.getChildAt(tabNavigate.selectedIndex);
					if (displayView is LinchuangGeneralInfoView)
					{
						var displayInfoView:LinchuangGeneralInfoView = displayView as LinchuangGeneralInfoView;
						displayInfoView.init(data.result as XML);
					}	
				}
			}
		
		}
		
		public function fault(info:Object):void
		{
			Alert.show(info.fault.message, "错误");
		}
		
		public function LinChuangGeneralSearchCommand()
		{
		}

	}
}