// ~1200 most common English words — covers ~85% of typical English text.
// Used to detect gibberish vs genuine writing.

const RAW = `
a abandon ability able about above abroad absence absolute absolutely absorb abuse academic accept
access accident accompany accomplish according account accurate accuse achieve achievement acid
acknowledge acquire across act action active activity actor actual actually ad adapt add addition
additional address adequate adjust administration administrator admire admit adopt adult advance
advanced advantage adventure advice advise adviser affect afford afraid after afternoon again against
age agency agenda agent aggressive ago agree agreement ahead aid aim air aircraft airline airport alarm
album alcohol alive all alliance allow ally almost alone along already also alter alternative although
always amazing amount analysis analyst analyze ancient and anger angle angry animal announce annual
another answer anticipate anxiety any anybody anymore anyone anything anyway anywhere apart apartment
apparent apparently appeal appear appearance apple application apply appointment approach appropriate
approval approve area argue argument arise arm army around arrange arrest arrival arrive art article as
aside ask asleep aspect assault assert assess assessment asset assign assignment assist assistance
assistant associate association assume assumption at atmosphere attach attack attempt attention attitude
attorney attract attractive attribute audience author authority available average avoid award aware
awareness away awful

baby back background bad badly bag bake balance ball ban band bank bar barely barrel barrier base
baseball basic basically basis basket basketball bathroom battery battle be beach bean bear beat
beautiful beauty because become bed bedroom beer before began begin beginning behavior behind being
belief believe bell belong below bench bend beneath benefit beside besides best better between beyond
bible big bike bill billion bind bit bite black blade blame blank blanket blast blind block blood blow
blue board boat body bomb bombing bond bone book boom boot border born borrow boss both bother bottle
bottom bound boundary bowl box boy boyfriend brain brand brave bread break breakfast breast breath
breathe brick bridge brief briefly bright brilliant bring broad broke brother brown brush buck budget
bug build builder building bullet bunch burden burn bury bus business busy but butter button buy buyer
by

cabin cabinet cable cake calculate call camera camp campaign campus can candidate cap capable capacity
capital capture car carbon card care career careful carefully carry case cash cast cat catch category
catholic cause celebrate cell center central century certain certainly chain chair chairman challenge
champion championship chance change channel chapter character characteristic charge charity chart chase
cheap check cheek cheese chest chicken chief child childhood chip chocolate choice choose church
circle circumstance cite citizen city civil civilian claim class classic classroom clean clear clearly
climate climb clinical clock close closely closer clothes club clue cluster coach coalition code coffee
cognitive cold collapse colleague collect collection collective college colonial color column combination
combine come comfort comfortable command commander comment commercial commission commit commitment
committee common communicate communication community company compare comparison compete competition
competitive competitor complain complaint complete completely complex complicated component compose
composition comprehensive computer concentrate concentration concept concern condition conduct
conference confidence confirm conflict confront confusion congress connect connection consciousness
consensus consequence conservative consider considerable consideration consist consistent constant
constantly constitute constitutional construct construction consultant consumer consumption contact
contain container contemporary content contest context continue contract contrast contribute
contribution control controversial controversy convention conventional conversation convert conviction
convince cook cookie cooking cool cooperation cop copy core corner corporate correct correspond cost
could council count counter country county couple courage course court cousin cover coverage crack
craft crash crazy cream create creation creative creature credit crew crime criminal crisis criteria
critic critical criticism criticize crop cross crowd crucial cry cultural culture cup curious current
currently curriculum custom customer cut cycle

dad daily damage dance danger dangerous dare dark darkness data database date daughter day dead deal dear
death debate decade decide decision deck declare decline deep deeply defeat defend defendant defense
defensive deficit define definitely definition degree delay deliver delivery demand democracy democrat
democratic department depend dependent depending deploy depression derive describe description desert
deserve design designer desire desk desperate despite destroy destruction detail detailed detect
determine develop developer development device devote dialogue die diet differ difference different
differently difficult difficulty dig digital dimension dinner direct direction directly director dirt
dirty disability disagree disappear discipline discourse discover discovery discrimination discuss
discussion disease dish dismiss disorder display dispute distance distant distinct distinction
distinguish distribute distribution district diverse diversity divide division doctor document dog
dollar domestic dominant dominate door double doubt down downtown dozen draft drag drama dramatic
dramatically draw drawing dream dress drink drive driver drop drug dry due during dust duty

each ear early earn earnings earth ease easily east eastern easy eat economic economy edge edition
editor educate education educational educator effect effective effectively efficiency effort eight either
elderly elect election electric electricity electronic element eliminate elite else elsewhere email
embrace emerge emergency emission emotion emotional emphasis emphasize empire employ employee employer
employment empty enable encounter encourage end enemy energy enforcement engage engine engineer
engineering enhance enjoy enormous enough ensure enter enterprise entertainment entire entirely entrance
entry environment environmental episode equal equally equipment era error escape especially essay
essential essentially establish establishment estate estimate evaluate evaluation even evening event
eventually ever every everybody everyday everyone everything everywhere evidence evil evolution evolve
exact exactly examination examine example exceed excellent except exchange exciting executive exercise
exhibit exhibition exist existence existing expand expansion expect expectation expense expensive
experience experiment expert explain explanation explicit explicitly explore explosion expose exposure
extend extension extensive extent external extra extraordinary extreme extremely eye

face facility fact factor factory faculty fail failure fair fairly faith fall familiar family famous fan
fantasy far farm farmer fashion fast fat fate father fault favorite fear feature federal fee feed feel
feeling fellow female fence few fewer fiction field fifteen fifth fifty fight fighter figure file fill
film final finally finance financial find finding fine finger finish fire firm first fish fit five fix
flag floor flow flower fly focus folk follow following food foot football for force foreign forest
forever forget form formal formation former formula forth fortune forward found foundation founder four
frame framework free freedom french frequency frequently fresh friend friendship from front fruit fuel
full fully fund fundamental funding furniture further furthermore future

gain galaxy game gang gap garage garden garlic gas gate gather gave gay gaze gear gender general
generally generate generation genetic gentleman gently german gesture get giant gift gifted girl
girlfriend give given glad glass global go goal god gold golden golf gone good govern government
governor grab grade gradually graduate grain grand grandfather grandmother grass grave gray great
greatest green grew ground group grow growing growth guarantee guard guess guest guide guilty gun guy

habit hair half hall hand handle hang happen happy hard hardly hat hate have he head headline
headquarters health healthy hear hearing heart heat heavy height hell hello help helpful her here
heritage hero herself hey hi hide high highlight highly hill him himself hip hire his historian
historic historical history hit hold hole holiday home hope hopefully horror horse hospital host hot
hotel hour house household housing how however huge human humor hundred hungry hurt husband

idea ideal identification identify identity ignore ill illegal illustrate image imagine imagination
immediate immediately immigrant immigration impact implement implication imply importance important
impose impossible impress impression impressive improve improvement in incident include including
increase increasingly incredible incredibly indeed independence independent index indian indicate
indication individual industrial industry infant infection inflation influence inform information
initial initially initiative injury inner innocent innovation input inquiry inside insight insist
install instance instead institution institutional instruction instructor instrument insurance
intellectual intelligence intend intense intention interest interested interesting internal
international internet interpretation intervention interview into introduce introduction invasion
invest investigation investigator investment investor invitation involve involved iron island issue it
item its itself

jacket jail job join joint joke journal journalist journey joy judge judgment juice jump junior jury
just justice justify

keen keep key kick kid kill kind king kiss kit kitchen knee knew knife knock know knowledge known

lab label lack lady lake land landscape language large largely laser last late lately later latter laugh
launch law lawn lawsuit lawyer lay layer lead leader leadership lean learn learning least leather leave
left leg legal legend legislation legitimate lemon length less lesson let letter level liberal library
lie life lifestyle lift light like likely limit limitation limited line link lip list listen literally
literary literature little live living load loan local locate location lock long look lord lose loss
lost lot loud love lovely lover low lower luck lunch lung

machine mad magazine mail main mainly maintain major majority make maker male mall man manage
management manager manner manufacturer manufacturing many map margin mark market marketing marriage
married marry mask mass massive master match material math matter may maybe mayor me meal mean meaning
meanwhile measure measurement meat mechanism media medical medication medicine medium meet meeting member
membership memory mental mention mentor menu mere merely message method middle might military milk
million mind mine minister minor minority minute miracle mirror miss mission mistake mix mixture model
moderate modern modest mom moment money monitor month mood more moreover morning mortgage most mostly
mother motion motivation mount mountain mouse mouth move movement movie much multiple murder muscle
museum music musical muslim must mutual my myself mystery myth

name narrative narrow nation national natural naturally nature near nearby nearly necessarily necessary
neck need negative negotiate negotiation neighbor neighborhood neither nerve network never nevertheless
new newly news newspaper next nice night nine no nobody nod noise nomination none nor normal normally
north northern nose not note nothing notice notion novel now nowhere number numerous nurse

object objection obligation observation observe observer obstacle obtain obvious obviously occasion
occasionally occupy occur ocean odd odds of off offense offensive offer office officer official often
oil ok old on once one ongoing online only onto open opening operate operation operator opinion
opponent opportunity oppose opposite opposition option or order ordinary organic organization organize
orientation origin original other otherwise ought our out outcome outside overcome overlook owe own
owner

pace pack package page paid pain paint painting pair pale pan panel pant paper parent park parking
part partially participate particular particularly partly partner partnership party pass passage
passenger past path patient pattern pause pay payment peace peak peer penalty people per percent
percentage perception perfect perfectly perform performance perhaps period permanent permission permit
person personal personality personally perspective phase phenomenon philosophy phone photo photograph
photography phrase physical physically physician piano pick picture pie piece pilot pine pink pipe
pitch place plan plane planet planning plant plastic plate platform play player please pleasure plenty
plus pocket poem poet poetry point police policy political politically politics pollution pool poor
pop popular population porch port portion portrait portray pose position positive positive possibility
possible possibly post potential potentially pound pour poverty power powerful practical practice pray
prayer precisely predict prefer preparation prepare prepared presence present presentation preserve
presidency president presidential press pressure pretend pretty prevent previous previously price
primarily primary prime principal principle print prior priority prison prisoner privacy private
probably problem procedure proceed process produce producer product production profession professional
professor profile profit program progress project promise promote promotion proof proper properly
property proportion proposal propose proposed prosecutor prospect protect protection protein protest
prove provide provider province provision psychological psychologist psychology public publication
publicly pull punishment purchase pure purpose pursue push put

qualify quality quarter quarterback question quick quickly quiet quietly quite quote

race racial racism racist radical radio rain raise range rank rapid rapidly rare rarely rate rather
rating reach react reaction read reader reading ready real realistic reality realize really reason
reasonable rebel recall receive recent recently recognition recognize recommend recommendation record
recover recovery recruit red reduce reduction reflect reflection reform region regional register regular
regulation reinforce reject relate relation relationship relative relatively release relevant relief
religion religious reluctant rely remain remaining remarkable remember remind remote remove repeat
repeatedly replace reply report reporter represent representation representative republic republican
reputation request require requirement research researcher resident resist resistance resolution resolve
resort resource respond response responsibility responsible rest restaurant restore restriction result
retain retire retirement return reveal revenue review revolution rhythm rice rich rid ride rifle right
ring rise risk river road rock role roll romantic roof room root rope rose rough roughly round route
row rule run running rural rush

safe safety sake salad salary sale salt same sample sanction sand satellite satisfaction satisfy save
saving say scale scandal scene schedule scholar scholarship school science scientific scientist scope
score screen sea search season seat second secondary secret secretary section sector secure security
seek seem segment seize select selection self sell senate senior sense sensitive sentence separate
sequence series serious seriously serve service session set setting settle settlement seven several
severe shake shall shape share sharp she sheet shelf shell shelter shift shine ship shirt shock shoot
shooting shop shopping shore short shortage shortly shot should shoulder shout show shower shut shut
side sight sign signal significance significant significantly silence silent silver similar similarly
simple simply simultaneously since sing singer single sir sister sit site situation six size ski skill
skin sky sleep slice slide slight slightly slip slow slowly small smart smell smile smoke smooth snap
so so-called social society soft software soil solar soldier solid solution solve some somebody someday
somehow someone something sometimes somewhat somewhere son song soon sophisticated sorry sort soul
sound source south southern space spanish speak speaker special specialist specialized specific
specifically speech speed spend spirit spiritual split spokesman sport spot spread spring squad
stability stable staff stage stair stake stand standard standing star stare start state statement
station status statute stay steady steal steel step stick still stock stomach stone stop store storm
story straight strange stranger strategic strategy stream street strength stress stretch strike string
strip stroke strong strongly structure struggle student studio study stuff stupid style subject submit
subsequent substance substantial succeed success successful successfully such suddenly suffer sufficient
sugar suggest suggestion suit summer sun super supply support supporter suppose sure surely surface
surgery surprise surprised surprising surround surrounding survey survival survive survivor suspect
suspend suspicion sustain swear sweet swim swing switch symbol symptom system

table tactic tail take tale talent talk tank tape target task tax taxpayer tea teach teacher teaching
team tear technology telephone television tell temperature ten tend tendency term terms terrible test
testify testing text than thank that the theater their them theme themselves then theory therapy there
therefore these they thick thin thing think thinking third this those though thought thousand threat
threaten three throat through throughout throw thus ticket tie tight time tiny tip tire tired title to
today toe together tomorrow tone tonight too tool top topic toss total totally touch tough tour tourist
toward towards tower town toy trace track trade tradition traditional traffic trail train training
transfer transform transformation transition translate transportation travel treat treatment tree
trend trial tribe trick trip troop trouble truly trust truth try tube turn tv twelve twenty twice twin
two type typical typically

ugly ultimate ultimately unable uncle under undergo understand understanding unemployment unfair
unfortunately unhappy uniform unify union unique unit united unity universal universe university
unknown unless unlike unlikely until up upon upper urban urge us use used useful user usual usually
utility

vacation valley valuable value variable variation variety various vast vehicle venture version versus
very veteran victim victory video view viewer village violation violence virtual virtually visible
vision visit visitor visual vital voice volume voluntary volunteer vote voter vs vulnerable

wage wait wake walk wall want war warning wash watch water wave way we weak weakness wealth weapon wear
weather web website wedding week weekend weekly weigh weight welcome welfare well west western wet what
whatever wheel when whenever where whereas wherever whether which while whisper white who whole whom
whose why wide widely widespread wife wild will willing win wind window wine wing winner winter wire
wisdom wise wish with withdraw without witness woman wonder wonderful wood word work worker working
world worry worse worst worth would wound wrap write writer writing wrong

yard yeah year yell yellow yes yesterday yet yield you young youngster your yourself youth
`.trim();

export const COMMON_WORDS: Set<string> = new Set(
  RAW.split(/\s+/).map((w) => w.toLowerCase().replace(/[^a-z'-]/g, "")).filter(Boolean)
);
