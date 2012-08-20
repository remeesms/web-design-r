package org.zkoss.zssex.init;

import org.zkoss.lang.Library;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.DesktopCleanup;
import org.zkoss.zk.ui.util.DesktopInit;
import org.zkoss.zk.ui.util.EventInterceptor;
import org.zkoss.zk.ui.util.ExecutionInit;
import org.zkoss.zk.ui.util.SessionCleanup;
import org.zkoss.zk.ui.util.SessionInit;
import org.zkoss.zssex.util.ObfuscatedString;

public class Init implements ExecutionInit, DesktopInit, DesktopCleanup, SessionInit, SessionCleanup, EventInterceptor{

	
  public static final String KEYPWD = new ObfuscatedString(new long[] { -9017617134232705315L, -3067316756544620689L, -7174741455541659722L, 9223059116147577819L, -7389013047307896124L }).toString();
	
	  public static final String UNIVERSAL_ACTIVE_CODE = new ObfuscatedString(new long[] { -8406957250436102276L, 7739080853276730995L, 2446544582485916568L, 4141763441450886800L, 9163385588230618037L, 3078723322430394289L }).toString();
	  public static final String ACTIVE_CODE = new ObfuscatedString(new long[] { 2927261568102559820L, 5003116366233426486L, -19977163214018644L }).toString();
	  public static final String LICENSE_SUBJECT = new ObfuscatedString(new long[] { 3902422651215371349L, 8624385350820619417L, -642393240400619653L }).toString();
	  public static final String UP_TIME = new ObfuscatedString(new long[] { 7735875781470822017L, 7449797441543358756L }).toString();
	  public static final String SESSION_COUNT = new ObfuscatedString(new long[] { 3229559844559651902L, 4507837234696327046L, 7727269190283665611L }).toString();
	  public static final String USER_NAME = new ObfuscatedString(new long[] { -7156956517562311570L, -4569457626895068567L, -9130454531809692735L }).toString();
	  public static final String COMPANY_ID = new ObfuscatedString(new long[] { -3429614419320347732L, 5284083613633888871L, -2962259370126176133L }).toString();
	  public static final String COMPANY_UNIT = new ObfuscatedString(new long[] { 4535911707703337843L, -5339041739265159617L, -8006648055853745059L }).toString();
	  public static final String COMPANY_NAME = new ObfuscatedString(new long[] { -7340139527016707886L, -5203243759892677212L, 8714822623115369524L }).toString();
	  public static final String COMPANY_CITY = new ObfuscatedString(new long[] { -6925611054058810462L, 2143563004714040551L, 1994584055053707741L }).toString();
	  public static final String COMPANY_ADDRESS = new ObfuscatedString(new long[] { -8691848786421899489L, -4370996558863340632L, -2709933490946981238L }).toString();
	  public static final String COMPANY_ZIPCODE = new ObfuscatedString(new long[] { -4934501068656857753L, -6913828373145765012L, 1063193634233753528L }).toString();
	  public static final String COUNTRY = new ObfuscatedString(new long[] { -4504969373906269801L, 5248137891553083335L }).toString();
	  public static final String PROJECT_NAME = new ObfuscatedString(new long[] { 830623621279886032L, 459360910074040759L, 4178520520701877821L }).toString();
	  public static final String PRODUCT_NAME = new ObfuscatedString(new long[] { 5504882338648710617L, -4761731334749763195L, 996375918662338065L }).toString();
	  public static final String PACKAGE = new ObfuscatedString(new long[] { -8439029564924938530L, -4878278112849633009L }).toString();
	  public static final String VERSION = new ObfuscatedString(new long[] { -4847689528984584834L, 2493253216426408014L }).toString();
	  public static final String ISSUE_DATE = new ObfuscatedString(new long[] { -4228764154858292882L, -6898004159031332466L, 6328666951570048917L }).toString();
	  public static final String EXPIRY_DATE = new ObfuscatedString(new long[] { -7233890858958970371L, -3423973165832030856L, -5810612950970282077L }).toString();
	  public static final String TERM = new ObfuscatedString(new long[] { -6725301182108235475L, -8691110408124621856L }).toString();
	  public static final String VERIFICATION_NUMBER = new ObfuscatedString(new long[] { 3823288740853721680L, -6436340937747658512L, 891079956768101415L, 751513662528431611L }).toString();
	  public static final String INFORMATION = new ObfuscatedString(new long[] { 378870925371295609L, 1418863983102047429L, -5017007170548372422L }).toString();
	  public static final String KEY_SIGNATURE = new ObfuscatedString(new long[] { -2573177027008659676L, 5066716785755217927L, 5769746383701090690L }).toString();
	  public static final String CHECK_PERIOD = new ObfuscatedString(new long[] { -2439022525501632135L, 6139476070014855270L, -297657911147084449L }).toString();
	  public static final String LICENSE_DIRECTORY_PROPERTY = new ObfuscatedString(new long[] { -1882551029428330572L, -8700125708888889285L, 2264098143846024619L, 8696977543803722267L, -3002719197410454636L, -5542261237218261145L }).toString();
	  public static final String LICENSE_VERSION = new ObfuscatedString(new long[] { -7080462743270045357L, 6928867389785115158L, -154565539896996742L }).toString();
	  public static final String WARNING_EXPIRY = new ObfuscatedString(new long[] { -2088056424898980973L, -3616911578495445651L, -8353968737700076168L }).toString();
	  public static final String WARNING_PACKAGE = new ObfuscatedString(new long[] { 7436618834759965309L, 8220698497085578148L, -6394078374620879850L }).toString();
	  public static final String WARNING_VERSION = new ObfuscatedString(new long[] { 7417971821667979026L, 7464186339852802771L, 7986314911006223431L }).toString();
	  public static final String WARNING_COUNT = new ObfuscatedString(new long[] { -1510608780643214737L, 6313704540210276937L, 4115504365890483558L }).toString();
	  public static final String WARNING_NUMBER = new ObfuscatedString(new long[] { 8367990676393660796L, -7163797910637480555L, -8349581027556623805L }).toString();
	  public static final String ZK_SPREADSHEET = new ObfuscatedString(new long[] { -7746426490831650241L, 7328409540860500523L, 3962011379583748974L }).toString();
	  
	  public static final String WARNING_EVALUATION = new ObfuscatedString(new long[] { 6293244219968982799L, -645771368638276165L, -7164593841888995318L, 3056658804758229281L, 2012533240900282186L, 4720458417030845876L, -649430094189861973L, -7918770929915836186L, 8851560671444236411L, 5606256511022648529L, 5213202847092904516L, -4102203601017740787L, -1032764338499136803L, 4632877787254627128L, 8652922073494088803L, 1017530493307865386L, 6363365592583820006L, -7614762895336268528L, -6558480611654840415L, -4743081489003502949L, -5862139290226956563L, -4583888483821845908L, -8745462703135464897L, -4873155536263193767L, -3881387260484276693L, 7581140259249343580L, -4703654983362907694L, 3642210020844866491L, -1873892967537094428L, 7431679954734481217L, 3464433087932489116L, -6024453854700727835L, 1210446632208205992L, -9057692879567092531L, 7829162361400083263L, -1875570117283775615L, -380845106451795279L, -1051776431017832137L, 4471385941321627797L, 495484629773446438L, 8597275517967444933L, 4298086683676932604L, 3253256358929010255L, -1508885386650347107L, -2790008182791469194L }).toString();
	  public static final String WARNING_INVALID_FILE = new ObfuscatedString(new long[] { -2705066582748869478L, -1756369395929404428L, 1927453613245088482L, 6002136807036289424L, -6927777686023936085L, 1793080898558536723L, 2768920254900639255L, -8466497755674134272L, 3853433474927584867L, -7035948665085087086L, 4139673979409662216L, 4556685628643930044L, -1505202977284950338L, 4372657461200298826L, 870565935216128439L, 715552231683639969L, -8440532278422812207L, 2275407799776572744L, -8628004182635503887L, 1441870243200925175L, 6760545480909666478L, -6868020178348304123L, -7081375598839201987L, 7578485673433694811L, -2865398942871624260L, 4146894525708705472L, -3083914055986203134L }).toString();
	  public static final String DEFAULT_LICENSE_DIRECTORY = new ObfuscatedString(new long[] { -5042984495264000326L, -7075224333823389221L, 2737249336978399967L, -2368002581952540878L }).toString();
	  public static final String UTEST = new ObfuscatedString(new long[] { -6252666169139603543L, -43763135309346748L, -5412364431233735340L, -1342277457322500721L, -3233034723380275725L, 8333057255774856663L, -7004928009668210393L }).toString();
	  public static final String ESCHERAGGREGATE_IMPL_0 = new ObfuscatedString(new long[] { -6126016284039316910L, -5242181267678831515L, 3626005944004206838L, 8355131686565987587L, 1442182626534055123L, 3784556152988695497L, -6440494607607095147L }).toString();
	  public static final String ZK_NOTICE = new ObfuscatedString(new long[] { -8347842002405430398L, 3574087500300642980L, -6273607823570371127L, -5524402949239665762L }).toString();
	  public static final String ZSS = new ObfuscatedString(new long[] { -8173181667746542109L, -7543777648745715290L, 1084351454888844032L }).toString();
	  public static final String EVAL_ONLY = new ObfuscatedString(new long[] { 8330515038476062730L, -435229286498387605L, -3853053404258091660L }).toString();
	  public static final String ZSS_EE = new ObfuscatedString(new long[] { 4874696179159791875L, 6828159996905510620L, 8997802664571284853L, 5297845084022046979L }).toString();
	  public static final String MD5STR_0 = new ObfuscatedString(new long[] { -5121064899839768052L, 3334273322282769989L }).toString();
	  public static final long[] KEY_SIG_CONST = { -8824876816566242848L, -406877062005977456L, -3332332678304402562L, -2945723887764541455L };
	  public static final String PUB_STORE = new ObfuscatedString(new long[] { 2986000131224251471L, 5740577535729811732L, -1784932956938255785L, -5053419831224178707L }).toString();
	  public static final String SUBJECT = new ObfuscatedString(new long[] { -5414105211624051485L, 5101764520790868466L, 7905655771772154682L, 7706749818695188070L }).toString();
	  public static final String KEY_NODE = new ObfuscatedString(new long[] { 3709353003282642825L, -2748448393049160943L, 3173082663019039626L }).toString();
	  public static final String V0 = new ObfuscatedString(new long[] { 6780048183396145217L, -3514785424911510459L }).toString();
	  public static final String V1 = new ObfuscatedString(new long[] { -8556922120573852888L, 708543790670807158L }).toString();
	  public static final String ALIAS = new ObfuscatedString(new long[] { -2751357802016299199L, 4066217211348802619L, -2064662869185498634L, 4043793295118034741L, 6227175189710674534L }).toString();
	  public static final String STORE_PASS = new ObfuscatedString(new long[] { -8677088790027852212L, 4602056908258993522L, 8019503246186939872L, -1944004741470673738L, -7033589056015316549L }).toString();
	  
  public static final String WIN = new ObfuscatedString(new long[] { 5321153595986820916L, 6399668227042605266L, -5602491487233658872L, 2066377234886365691L, 203726272017906087L, -3409963818121804467L, -290525610284193589L, 5360349214446654186L, 7126235572874868104L, 6443646263932522040L, -2888496393462639896L }).toString() + new ObfuscatedString(new long[] { 8156116825536686571L, -7933011286180755110L, 8429279178335537173L, 5140564363110783572L, -4587992236270359981L, -1850871503295801036L, -7368992273418428298L, -9139769631895394533L, 901440478462063480L, 1371905052599390164L, -5107498036593385759L, -5563648405980080527L, 2015340681356193425L, 916270472538173639L, 7391668993988648096L, 5028903049363742715L, -2699427368479864385L, -2350362590362555178L }).toString();
  public static final String BOOKCTRL = new ObfuscatedString(new long[] { 7217622360120277694L, -8400683914818061891L, -8473661553457508580L, -5622228806515116163L, -4622988458265486119L, 7446926302933303411L }).toString();
  public static final String BOOKCTRL_IMPL = new ObfuscatedString(new long[] { 8366566480343813638L, -8972442892763632473L, -2580875935722009905L, 3294093270838727536L, -8770059996803291093L, 4756737774858968358L }).toString();
  public static final String FUNCTION_RESOLVER = new ObfuscatedString(new long[] { -5782386627373366589L, 1603982320953709168L, -5756822281752573066L, -7336818521988074315L, -1131546475408671923L, 7159036251941172736L, 489241223135242132L }).toString();
  public static final String FUNCTION_RESOLVER_IMPL = new ObfuscatedString(new long[] { -5787358203727949318L, 2841377786218160132L, -5218577953052677617L, -6070514537744184299L, -3440827359817674024L, 1979393917035314089L, -3931450453424944917L }).toString();
  public static final String ESCHERAGGREGATE = new ObfuscatedString(new long[] { -6735980303887891563L, 2457814035054200632L, -631298676849182360L, -4657124571619221840L, 9153792680896270032L, -7571229398855975418L, -3112825257918092277L }).toString();
  public static final String ESCHERAGGREGATE_IMPL = new ObfuscatedString(new long[] { -6126016284039316910L, -5242181267678831515L, 3626005944004206838L, 8355131686565987587L, 1442182626534055123L, 3784556152988695497L, -6440494607607095147L }).toString();
  public static final String SHEETCTRL = new ObfuscatedString(new long[] { -3231807040389489489L, 5552506460595962851L, -3017483753344449122L, 8658433836752236494L, 9022269525009855167L, -1571966035218946254L }).toString();
  public static final String SHEETCTRL_IMPL = new ObfuscatedString(new long[] { 1677797888157201624L, -2491304950684197657L, -2300786629882468970L, -4286589672684229590L, 6199706450696319274L, -3644741510170656917L }).toString();
  public static final String EXPORTER = new ObfuscatedString(new long[] { -1079619589971504139L, 4018679606046498135L, -5426530123301717851L, 5737704374688555710L, 4573794936369932991L, -3932552322023266488L, -3527273792430485705L }).toString();
  public static final String EXPORTER_IMPL = new ObfuscatedString(new long[] { -7834075951217116515L, -1111578564406812452L, 4520764191002412166L, 5869317830377480262L, 652381227885528441L, 1744377079709691612L, -4449666564017326026L, -5124729765938846892L, -654480075963104636L, 3825584076585672292L, 3829641581879780642L, -7030304356420171021L, -6956750623539308174L }).toString();
  public static final String FUNCTIONS = new ObfuscatedString(new long[] { -9107950682079466537L, 6044098016258491637L, 4752071218608557149L, -1415194819165792974L, 1037012558002603534L, -286936863091228478L }).toString();
  public static final String FUNCTIONS_IMPL = new ObfuscatedString(new long[] { 4840889417712456605L, -932238802067396981L, 1643890773373654566L, -4649133848658165041L, 5968888824247950491L, -7413278661051192632L, 2152792200966762843L, -7577240830758492503L, -6433816155231039363L, 8826133207976952688L }).toString();
  public static final String WIDGET_HANDLER = new ObfuscatedString(new long[] { -4996559347472087276L, -7766264256509841091L, 4421449018288163029L, 1989371856569290729L, 6120813221538358633L, -4795921775128256392L }).toString();
  public static final String WIDGET_HANDLER_IMPL = new ObfuscatedString(new long[] { 7936749289072989851L, 8075841980845954183L, 3847322226299015273L, 6066080613497293258L, 1620474641933788511L, -1548118456145909793L, -1418230738701484438L }).toString();
  public static final String WIDGET_LOADER = new ObfuscatedString(new long[] { 5817538321180584438L, 8865508355996080707L, 4886347968546669731L, 435526161500103674L, -8511131547026821770L, 5742632421264337498L }).toString();
  public static final String WIDGET_LOADER_IMPL = new ObfuscatedString(new long[] { -933027784286545804L, -6380004711020883432L, -8028339963317745976L, 1225777306763359407L, 7409495186831786856L, -241248085354424324L, -5246784403500936783L, 2337188762834458524L }).toString();

  public static final String MD5STR = new ObfuscatedString(new long[] { -5121064899839768052L, 3334273322282769989L }).toString();
  public static final String MD = new ObfuscatedString(new long[] { 2345590606122015185L, -5069608179148319545L }).toString();
  public static final String TITLE = new ObfuscatedString(new long[] { 6290325967589686282L, 5925669707962544096L }).toString();
  public static final String BORDER = new ObfuscatedString(new long[] { 1517003640222100403L, -5881717338632855477L }).toString();
  public static final String MODE = new ObfuscatedString(new long[] { 2063230648775789892L, -7765346573662599107L, -3183841938540764871L }).toString();
  public static final String WIDTH = new ObfuscatedString(new long[] { -2028303217529921622L, 6478417485293582031L }).toString();
  public static final String HEIGHT = new ObfuscatedString(new long[] { 2974696421778732584L, -3701582788618461277L }).toString();
  public static final String ARG = new ObfuscatedString(new long[] { 3689490926743112102L, -8583085698292004623L }).toString();
  public static final String UPTIME_EXP = new ObfuscatedString(new long[] { 836794356891112142L, 3595395733708118761L, -7978618701001773448L, -7711157499660008450L, 9143275964856405829L, -8978572650370272471L, -1885816955389459868L, -2850716340587082295L }).toString();
  public static final String TRIAL_EXP = new ObfuscatedString(new long[] { 6254089562974914622L, -3227072154423754334L, -4406587499255443939L, -6016561217416014811L, 1053770341113116349L, 6895376768732924359L }).toString();
  public static final String SESSION_EXP = new ObfuscatedString(new long[] { 1982171942942774777L, -8787355027292006585L, -1691495013518805258L, -4266241046547582566L, 6831110504825143746L, -5394459224874436555L, 3957820018767187526L, 1032975061284734343L }).toString();
  public static final String UPTIME_INFO = new ObfuscatedString(new long[] { -140629977948033438L, 8411039821423448906L, -3474650949739896324L, 2772224587032503194L }).toString();
  public static final String SESSION_INFO = new ObfuscatedString(new long[] { -282132617423556107L, 2757430579228438986L, -2300236627084260024L, 5486533064596330289L }).toString();
  public static final String LIC_INFO = new ObfuscatedString(new long[] { -1327671782532263310L, 840490956016971870L, 3061901067660749900L }).toString();
  public static final String MASK_HEAD = new ObfuscatedString(new long[] { 2610432342261686015L, 2813741495629532862L, -1181939021078443460L, -7375419926652952589L }).toString();
  public static final String MASK_BODY = new ObfuscatedString(new long[] { -776441849887137687L, -9143613716996647403L, 4380415369023607826L, 1307662236470395652L, -689297554901065354L, -4590698981075227578L, 8398261592963428925L, -2272630810702424214L, 5009103648726399196L, -176307024327010546L }).toString();
  public static final String MASK_FOOT = new ObfuscatedString(new long[] { 812264288056794049L, -2332289316366425421L }).toString();

  public static final String EVAL_LIC_ID = new ObfuscatedString(new long[] { -2799362229550136139L, -8991339801923478651L, 2054766831045471090L }).toString();
  public static final String EVAL_LIC_VER = new ObfuscatedString(new long[] { 4515837249784318634L, -483965117545718936L }).toString();
  public static final String EVAL_USER_NAME = new ObfuscatedString(new long[] { 6700787382705499563L, 2359114071391082446L, 1949693015831576717L }).toString();
  public static final String EVAL_COMPANY_ID = new ObfuscatedString(new long[] { 8997629646135421336L, -7459597106373371040L, 3324566901651534592L }).toString();
  public static final String EVAL_COMPANY_NAME = new ObfuscatedString(new long[] { -7105953592593293452L, -8116579615415252408L, -3117127378177258899L, 9036340616621476990L }).toString();
  public static final Long EVAL_SESSION_COUNT = Long.valueOf(9223372036854775807L);
  
  
  public Init() {
	  
//	  org.zkoss.zss.model.BookCtrl.class
//	  org.zkoss.zssex.model.impl.BookCtrlImpl
	  Library.setProperty(BOOKCTRL, BOOKCTRL_IMPL);
	  
//	  org.zkoss.zss.formula.FunctionResolver.class
//	  org.zkoss.zssex.formula.ZKFunctionResolver
	  Library.setProperty(FUNCTION_RESOLVER, FUNCTION_RESOLVER_IMPL);
	  
//	  org.zkoss.zss.model.EscherAggregate.class
//	  org.zkoss.zssex.model.impl.ZKEscherAggregate
	  Library.setProperty(ESCHERAGGREGATE, ESCHERAGGREGATE_IMPL);
	  
//	  org.zkoss.zss.model.impl.SheetCtrl.class
//	  org.zkoss.zssex.model.impl.SheetCtrlImpl
	  Library.setProperty(SHEETCTRL, SHEETCTRL_IMPL);
	  
//	  org.zkoss.zssex.model.default.Exporter.class
//	  pdf=org.zkoss.zss.model.impl.pdf.PdfExporter,html=org.zkoss.zss.model.impl.html.HtmlExporter
//      Library.setProperty(EXPORTER, EXPORTER_IMPL);
	  
//	  http://www.zkoss.org/zss/functions
//	  financial,engineering,math,statistical,text,info,datetime,logical
//      Library.setProperty(FUNCTIONS, FUNCTIONS_IMPL);
	  
//	  org.zkoss.zss.ui.sys.WidgetHandler.class
//	  org.zkoss.zssex.ui.widget.DefaultWidgetHandler
	  Library.setProperty(WIDGET_HANDLER, WIDGET_HANDLER_IMPL);
	  
//	  org.zkoss.zss.ui.sys.WidgetLoader.class
//	  org.zkoss.zssex.ui.widget.DefaultBookWidgetLoader
      Library.setProperty(WIDGET_LOADER, WIDGET_LOADER_IMPL);  
  }
  
  public static final boolean doInit(WebApp paramWebApp, boolean paramBoolean) 
  	throws Exception{
	  
//	  paramWebApp.setAttribute(ACTIVE_CODE, getActiveCode());
	  
	  paramWebApp.getConfiguration().addListener(Init.class);
	  
	  return true;
  }
  
  
	public void init(Execution arg0, Execution arg1) throws Exception {
	}
	
	public void init(Desktop arg0, Object arg1) throws Exception {
	}
	
	public void cleanup(Desktop arg0) throws Exception {
	}
	
	public void init(Session arg0, Object arg1) throws Exception {
	}
	
	public void cleanup(Session arg0) throws Exception {
	}
	
	public void afterProcessEvent(Event arg0) {
	}
	
	public Event beforePostEvent(Event arg0) {
		return arg0;
	}
	
	public Event beforeProcessEvent(Event arg0) {
		return arg0;
	}
	
	public Event beforeSendEvent(Event arg0) {
		return arg0;
	}
	
	public static void main(String[] args) {
		
		int a = 80;
		char b = (char) a;
		System.out.println(b);
		
		System.out.println( UTEST );
		System.out.println(ESCHERAGGREGATE_IMPL + "\n");
		
		System.out.println(BOOKCTRL);
		System.out.println(BOOKCTRL_IMPL + "\n");
		System.out.println(FUNCTION_RESOLVER);
		System.out.println(FUNCTION_RESOLVER_IMPL + "\n");
		System.out.println(ESCHERAGGREGATE);
		System.out.println(ESCHERAGGREGATE_IMPL + "\n");
		System.out.println(SHEETCTRL);
		System.out.println(SHEETCTRL_IMPL + "\n");
		System.out.println(EXPORTER);
		System.out.println(EXPORTER_IMPL + "\n");
		System.out.println(FUNCTIONS);
		System.out.println(FUNCTIONS_IMPL + "\n");
		System.out.println(WIDGET_HANDLER);
		System.out.println(WIDGET_HANDLER_IMPL + "\n");
		System.out.println(WIDGET_LOADER);
		System.out.println(WIDGET_LOADER_IMPL + "\n");	
	}
	
}
