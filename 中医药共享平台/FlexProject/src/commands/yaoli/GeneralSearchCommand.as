package commands.yaoli
{
	
	import business.delegates.yaoli.GeneralSearchDelegate;
	
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import events.yaoli.GeneralSearchEvent;
	
	import flexlib.containers.SuperTabNavigator;
	
	import model.TsfModelLocator;
	
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	
	import view.yaoli.GeneralInfoView;
	
	public class GeneralSearchCommand implements ICommand, IResponder
	{
		[Bindable]
		private var modelLocator:TsfModelLocator = TsfModelLocator.getInstance();
		
		public var key:String;
		public var cate:String;
		public var source:String;
		public var related:String;
		public var searchType:int;
		
		public function execute(event:CairngormEvent):void
		{
			var generalSearchEvent:GeneralSearchEvent = event as GeneralSearchEvent;
			key = generalSearchEvent.key;
			cate = generalSearchEvent.cate;
			source = generalSearchEvent.source;
			related = generalSearchEvent.related;
			searchType = modelLocator.searchType;
			var delegate:GeneralSearchDelegate = new GeneralSearchDelegate(this);
			delegate.search(key, cate, source, related, searchType);
		}
		
		public function result(data:Object):void
		{
			//Alert.show(data.toString());
			if (data.result != null)
			{
				if (data.result is XML)
				{				
					var tabNavigate:SuperTabNavigator = modelLocator.resultView.tabNav;
					var displayView:Object = tabNavigate.getChildAt(tabNavigate.selectedIndex);
					if (displayView is GeneralInfoView)
					{
						var displayInfoView:GeneralInfoView = displayView as GeneralInfoView;
						displayInfoView.init(data.result as XML);
					}	
				}
			}
		
		}
		
		public function fault(info:Object):void
		{
			Alert.show(info.fault.message, "错误");
		}		
		
		public function GeneralSearchCommand()
		{
		}

	}
}