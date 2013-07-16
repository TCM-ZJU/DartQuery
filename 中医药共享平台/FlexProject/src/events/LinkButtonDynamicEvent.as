package events
{
	import mx.events.DynamicEvent;

	 public class LinkButtonDynamicEvent extends DynamicEvent
	{
		public static const EVENT_ID:String = "DataGridLinkButtonClickEvent";
		
		public var key:String;
		public var source:String;
		public var label:String;

		public function LinkButtonDynamicEvent(type:String, key:String, source:String, label:String)
		{
			super(type,true);
			this.key = key;
			this.source = source;
			this.label = label;
		}
		
	} 
	/* public class LinkButtonDynamicEvent extends Event
	{
		public static const EVENT_ID:String = "DataGridLinkButtonClickEvent";
		
		public var rowObject:Object;

		public function LinkButtonDynamicEvent(type:String, value:Object)
		{
			super(type, true);
			this.rowObject = value;
		}
		
	} */
}