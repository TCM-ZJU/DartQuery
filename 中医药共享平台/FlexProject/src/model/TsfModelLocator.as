package model
{
	import com.adobe.cairngorm.model.IModelLocator;
	
	import vo.*;
	import vo.yaoli.Yaoli;
	
	[Bindable]
	public class TsfModelLocator implements IModelLocator
	{
		
		private static var _instance:TsfModelLocator;
		
		public function TsfModelLocator(enforer:SingletonEnforcer)
		{
			if (enforer == null)
			{
				throw new Error("You can only have one TsfModelLocator");
			}
		}
		
		public static function getInstance():TsfModelLocator
		{
			if (_instance == null)
			{
				_instance = new TsfModelLocator(new SingletonEnforcer());
			}
			return _instance;
		}
		public var resultPanelHeight:int = 1000;
		public var entryIndex:int;
		public var primaryEntryIndex:int;
		public var subEntryIndex:int;
		public var subComboEnable:Boolean = true;
		public var currentView:String;
		public var searchType:int = 1;
		public var searchDB:int = 0;
		public var resultView:Object;
		public var isIntro:Boolean = false;
		public var currentInfo:CurrentSearchVO = new CurrentSearchVO();
		public var generalResult:GeneralInfoVO = new GeneralInfoVO();
		public var linChuang:LinChuang = new LinChuang();
		public var yaoLi:Yaoli = new Yaoli();
		public var copyright:String = "© 2007-2012";
	//	public var icp:String = "<a href='http://www.miibeian.gov.cn/' target='_blank'>京ICP备05055726号</a>"
		public var icp:String = "京ICP备05055726号";
	}
}

class SingletonEnforcer{}