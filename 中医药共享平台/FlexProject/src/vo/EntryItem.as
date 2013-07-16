package vo
{
	import flash.utils.Dictionary;
	
	[Bindable]
	public class EntryItem
	{
		public var label:String = "";   //入口名称
		public var itemArray:Array = new Array();    //该入口显示的标签列表
		public var map:Object = new Object();     //标签名称与URI映射
		public function EntryItem()
		{
		}

	}
}