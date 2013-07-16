package events
{
	import flash.events.Event;

	public class ShowSourceEvent extends Event
	{
		public static const SHOW_SOURCE:String = "showSource";
		public var index:int;
		
		public function ShowSourceEvent(type:String, index:int)
		{
			super(type);
			this.index = index;
		}
		
		
		
		override public function clone():Event
		{
			return new ShowSourceEvent(type, index);
		}
		
	}
}