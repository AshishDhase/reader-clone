package com.learning.readingadventure

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlin.math.*
import kotlin.random.Random

private val Ink = Color(0xFF234C45)
private val Green = Color(0xFF187461)
private val Cream = Color(0xFFFFFAEC)
private val Gold = Color(0xFFFFD66D)
private val Coral = Color(0xFFFF9F87)
private val Sky = Color(0xFFDFEFF9)
private val Lilac = Color(0xFFD7C9F4)
private val TileColors = listOf(Gold, Coral, Lilac, Color(0xFFA9DCC8), Color(0xFFADD6ED))

class MainActivity : ComponentActivity() {
    private val model: GameViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); enableEdgeToEdge()
        setContent { MaterialTheme(colorScheme = lightColorScheme(primary = Green, background = Cream, surface = Cream, onSurface = Ink)) { AdventureApp(model) } }
    }
    override fun onStop() { model.voice.stop(); model.feedback.close(); super.onStop() }
}

@Composable
fun AdventureApp(model: GameViewModel) {
    var parentGate by rememberSaveable { mutableStateOf(false) }
    var settings by rememberSaveable { mutableStateOf(false) }
    var level by rememberSaveable { mutableIntStateOf(1) }
    val active = model.active
    BackHandler(active != null || parentGate || settings) { when { settings -> settings = false; parentGate -> parentGate = false; else -> model.home() } }
    Surface(Modifier.fillMaxSize(), color = Cream) {
        Column(Modifier.fillMaxSize().safeDrawingPadding().padding(horizontal = 20.dp)) {
            Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                if (active == null) Text("THE LITTLE WORD GARDEN", fontSize = 12.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
                else TextButton(onClick = model::home, modifier = Modifier.heightIn(min = 48.dp)) { Text("‹  Garden", fontSize = 18.sp) }
                TextButton(onClick = { model.voice.stop(); parentGate = true }, modifier = Modifier.heightIn(min = 48.dp)) { Text("Parents") }
            }
            if (active == null) {
                when {
                    model.error != null -> Text(model.error!!)
                    model.words.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                    else -> Garden(model, level, { level = it }, Modifier.weight(1f))
                }
            } else key(active.id) { PlayScreen(model, active, Modifier.weight(1f)) }
        }
    }
    if (parentGate) ParentGate(onDismiss = { parentGate = false }, onSuccess = { parentGate = false; settings = true })
    if (settings) ParentSettings(model) { settings = false; level = level.coerceAtMost(model.unlocked) }
}

@Composable
private fun Garden(model: GameViewModel, level: Int, setLevel: (Int) -> Unit, modifier: Modifier) {
    val compact = LocalConfiguration.current.screenHeightDp < 500
    Column(modifier) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Little words.\nBig adventures.", fontSize = if(compact) 26.sp else 34.sp, lineHeight = if(compact) 30.sp else 38.sp, fontWeight = FontWeight.ExtraBold)
                Text("Grow a word with Pip & Moss", Modifier.padding(top = 8.dp), fontSize = 16.sp, color = Ink.copy(alpha=.75f))
            }
            GardenScene(Modifier.size(if(compact) 92.dp else 118.dp), false, false, "pip")
        }
        Row(Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Seedlings", "Sprouts", "Bloomers").forEachIndexed { index, title ->
                val unlocked = index+1 <= model.unlocked
                FilterChip(selected = level == index+1, onClick = { if(unlocked) setLevel(index+1) }, label = { Text(if(unlocked) title else "$title · locked", fontSize=12.sp) }, modifier=Modifier.weight(1f).heightIn(min=48.dp), enabled=unlocked)
            }
        }
        Row(Modifier.fillMaxWidth().padding(bottom=10.dp), horizontalArrangement=Arrangement.SpaceBetween) {
            Text("Pick a word to grow", fontWeight=FontWeight.Bold, fontSize=18.sp)
            Text("${model.completed.size} / ${model.words.size}", color=Green)
        }
        if(model.unlocked < 3) Text("Grow 12 ${if(model.unlocked == 1) "seedling" else "sprout"} words to open the next garden.", fontSize=12.sp, modifier=Modifier.padding(bottom=10.dp))
        LazyVerticalGrid(columns=GridCells.Adaptive(100.dp), verticalArrangement=Arrangement.spacedBy(12.dp), horizontalArrangement=Arrangement.spacedBy(12.dp), contentPadding=PaddingValues(bottom=24.dp), modifier=Modifier.weight(1f)) {
            items(model.words.filter { it.difficulty == level }, key={it.id}) { word ->
                val grown = word.id in model.completed
                Surface(onClick={model.start(word)}, color=if(grown) Color(0xFFDDEDDD) else Color.White, shape=RoundedCornerShape(24.dp), border=BorderStroke(1.dp,Ink.copy(alpha=.08f)), modifier=Modifier.height(100.dp).semantics { contentDescription="Learn ${word.text}${if(grown) ", completed" else ""}" }) {
                    Column(horizontalAlignment=Alignment.CenterHorizontally, verticalArrangement=Arrangement.Center) {
                        Text(word.text, fontSize=28.sp, fontWeight=FontWeight.Bold)
                        Text(if(grown) "✦ grown" else "• • •", fontSize=12.sp, color=Green, modifier=Modifier.padding(top=4.dp))
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun PlayScreen(model: GameViewModel, word: LearningWord, modifier: Modifier) {
    val speaking by model.voice.speaking.collectAsStateWithLifecycle()
    val compact = LocalConfiguration.current.screenHeightDp < 500
    Column(modifier, horizontalAlignment=Alignment.CenterHorizontally) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween, verticalAlignment=Alignment.CenterVertically) {
            Column {
                Text(when(model.phase) { Phase.LETTERS -> "1  /  BUILD A WORD"; Phase.SENTENCE -> "2  /  TELL A STORY"; Phase.CELEBRATE -> "3  /  WATCH IT GROW" }, fontSize=11.sp, letterSpacing=2.sp, color=Green, fontWeight=FontWeight.Bold)
                Text(model.message, fontSize=if(compact) 17.sp else 20.sp, fontWeight=FontWeight.Bold, modifier=Modifier.padding(top=4.dp))
            }
            FilledTonalButton(onClick=model::repeat, modifier=Modifier.sizeIn(minWidth=64.dp,minHeight=52.dp).semantics { contentDescription="Repeat pronunciation" }) { Text("♪",fontSize=26.sp) }
        }
        if(model.phase == Phase.LETTERS) {
            LetterPlayfield(model, word, Modifier.weight(1f).fillMaxWidth())
            Text("Drag a letter — or tap a letter, then its home", fontSize=12.sp, color=Ink.copy(alpha=.7f), textAlign=TextAlign.Center, modifier=Modifier.padding(bottom=16.dp))
        } else {
            Row(Modifier.weight(1f).fillMaxWidth(), verticalAlignment=Alignment.CenterVertically, horizontalArrangement=Arrangement.Center) {
                if(compact) GardenScene(Modifier.weight(.4f).fillMaxHeight(), speaking, model.phase==Phase.CELEBRATE, word.character)
                Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(vertical=8.dp), horizontalAlignment=Alignment.CenterHorizontally) {
                    if(!compact) GardenScene(Modifier.height(170.dp).fillMaxWidth(),speaking,model.phase==Phase.CELEBRATE,word.character)
                    Text(word.text, fontSize=40.sp, fontWeight=FontWeight.ExtraBold, color=Green)
                    val sentence = if(model.phase==Phase.SENTENCE) word.sentence.replaceFirst(Regex("\\b${word.text}\\b",RegexOption.IGNORE_CASE), "_____" ) else word.sentence
                    Text(sentence, fontSize=24.sp, lineHeight=32.sp, textAlign=TextAlign.Center, modifier=Modifier.padding(20.dp))
                    if(model.phase==Phase.SENTENCE) {
                        Text("Tap the missing word", fontSize=14.sp)
                        val choices=remember(word.id) { (listOf(word.text)+model.words.filter { it.text!=word.text }.shuffled(Random(word.id.hashCode())).take(2).map{it.text}).shuffled(Random(word.id.hashCode())) }
                        FlowRow(Modifier.padding(top=12.dp),horizontalArrangement=Arrangement.spacedBy(8.dp), verticalArrangement=Arrangement.spacedBy(8.dp)) { choices.forEach { choice -> FilledTonalButton(onClick={model.sentenceChoice(choice)},modifier=Modifier.heightIn(min=56.dp)) { Text(choice,fontSize=21.sp) } } }
                    } else {
                        Button(onClick=model::next, modifier=Modifier.heightIn(min=60.dp).widthIn(min=220.dp)) { Text("Grow another word  →",fontSize=18.sp) }
                        TextButton(onClick=model::home,modifier=Modifier.heightIn(min=48.dp)) { Text("Back to the garden") }
                    }
                }
            }
        }
    }
}

@Composable
private fun LetterPlayfield(model: GameViewModel, word: LearningWord, modifier: Modifier) {
    val density = LocalDensity.current
    val speaking by model.voice.speaking.collectAsStateWithLifecycle()
    BoxWithConstraints(modifier) {
        val width = constraints.maxWidth.toFloat(); val height = constraints.maxHeight.toFloat()
        val columns = min(word.text.length, max(1, (width / with(density){52.dp.toPx()}).toInt()))
        val tile = min(with(density){64.dp.toPx()}, width / columns - with(density){4.dp.toPx()})
        val spacing = with(density){4.dp.toPx()}
        val left=(width-columns*(tile+spacing)+spacing)/2
        val shortField=height < with(density){240.dp.toPx()}
        val targetY=if(shortField) height*.08f else height*.39f
        val rows=(word.text.length+columns-1)/columns
        val trayY=min(height*.70f, height-rows*(tile+spacing)-8f)
        val order=remember(word.id){word.text.indices.shuffled(Random(word.id.hashCode()))}
        GardenScene(if(shortField) Modifier.align(Alignment.CenterStart).fillMaxWidth(.22f).fillMaxHeight(.9f) else Modifier.align(Alignment.TopCenter).fillMaxWidth().fillMaxHeight(.39f),speaking,false,word.character)
        word.text.forEachIndexed { slot, letter ->
            val x=left+(slot % columns)*(tile+spacing)
            val y=targetY+(slot / columns)*(tile+spacing)
            Box(Modifier.offset { IntOffset(x.roundToInt(),y.roundToInt()) }.size(with(density){tile.toDp()}).clip(RoundedCornerShape(16.dp)).background(if(model.hintSlot==slot) Gold.copy(alpha=.7f) else Ink.copy(alpha=.06f)).border(2.dp,if(model.hintSlot==slot) Green else Ink.copy(alpha=.18f),RoundedCornerShape(16.dp)).clickable { if(model.selected>=0) model.drop(model.selected,slot) }.semantics { contentDescription="Home for $letter, position ${slot+1}" },contentAlignment=Alignment.Center) {
                Text(letter.toString(),fontSize=with(density){(tile*.52f).toSp()},fontWeight=FontWeight.Bold,color=Ink.copy(alpha=.23f))
            }
        }
        word.text.forEachIndexed { index, letter ->
            val assigned=model.board.assignments[index]
            val homeX=left+(order.indexOf(index)%columns)*(tile+spacing)
            val homeY=trayY+(order.indexOf(index)/columns)*(tile+spacing)
            var drag by remember { mutableStateOf(Offset.Zero) }
            var dragging by remember { mutableStateOf(false) }
            val destinationX=if(dragging) homeX+drag.x else if(assigned!=null) left+(assigned%columns)*(tile+spacing) else homeX
            val destinationY=if(dragging) homeY+drag.y else if(assigned!=null) targetY+(assigned/columns)*(tile+spacing) else homeY
            val animatedX by animateFloatAsState(destinationX, if(dragging) snap() else tween(300,easing=Easing { Motion.snap(it) }),label="snap x")
            val animatedY by animateFloatAsState(destinationY, if(dragging) snap() else spring(dampingRatio=.6f,stiffness=350f),label="snap y")
            val idle=rememberInfiniteTransition(label="letter idle")
            val wiggle by idle.animateFloat(-3f,3f,infiniteRepeatable(tween(800+index*90),RepeatMode.Reverse),label="letter wobble")
            Box(Modifier.offset { IntOffset(animatedX.roundToInt(),animatedY.roundToInt()) }.size(with(density){tile.toDp()}).zIndex(if(dragging) 4f else 1f)
                .graphicsLayer { rotationZ=if(assigned==null) wiggle*word.animation.bounce else 0f; scaleX=if(dragging) 1.12f else 1f; scaleY=if(dragging) 1.18f else 1f }
                .shadow(if(dragging) 8.dp else 3.dp,RoundedCornerShape(16.dp)).clip(RoundedCornerShape(16.dp)).background(TileColors[index%TileColors.size])
                .border(if(model.selected==index) 3.dp else 0.dp,Green,RoundedCornerShape(16.dp))
                .pointerInput(word.id,index,assigned,width,height) {
                    if(assigned==null) detectDragGestures(onDragStart={dragging=true; model.select(index)},onDragCancel={drag=Offset.Zero;dragging=false},onDragEnd={
                        val cx=homeX+drag.x+tile/2; val cy=homeY+drag.y+tile/2
                        val slot=word.text.indices.minByOrNull { s ->
                            val sx=left+(s%columns)*(tile+spacing)+tile/2
                            val sy=targetY+(s/columns)*(tile+spacing)+tile/2
                            (cx-sx).pow(2)+(cy-sy).pow(2)
                        } ?: -1
                        val sx=left+(slot%columns)*(tile+spacing)+tile/2
                        val sy=targetY+(slot/columns)*(tile+spacing)+tile/2
                        if(abs(cx-sx)<=tile*.6f && abs(cy-sy)<=tile*.65f) model.drop(index,slot) else model.drop(index,-1)
                        drag=Offset.Zero;dragging=false
                    }) { change, amount -> change.consume(); drag+=amount }
                }.clickable(enabled=assigned==null){model.select(index)}.semantics { contentDescription="Letter $letter, tile ${index+1}${if(assigned!=null) ", placed" else ""}" },contentAlignment=Alignment.Center) {
                Text(letter.toString(),fontSize=with(density){(tile*.57f).toSp()},fontWeight=FontWeight.ExtraBold,color=Ink)
            }
        }
    }
}

/** Original vector characters. No imported image or animation assets. */
@Composable
private fun GardenScene(modifier: Modifier, speaking: Boolean, celebrating: Boolean, character: String) {
    val transition=rememberInfiniteTransition(label="garden animation")
    val seconds by transition.animateFloat(0f,6.283185f,infiniteRepeatable(tween(4000,easing=LinearEasing)),label="garden clock")
    Canvas(modifier.semantics { contentDescription=if(character=="pip") "Pip, a golden seed friend" else "Moss, a green leaf friend" }) {
        val unit=min(size.width,size.height); val cx=size.width/2; val cy=size.height*.57f
        drawOval(Color(0xFFE6EED9),Offset(cx-unit*.42f,cy+unit*.18f),Size(unit*.84f,unit*.2f))
        val bob=Motion.bounce(seconds,unit*.025f)
        val sway=if(celebrating) sin(seconds*3)*unit*.1f else sin(seconds)*unit*.02f
        translate(cx+sway,cy+bob) {
            rotate(sin(seconds)*4) {
                drawLine(Green,Offset(0f,-unit*.23f),Offset(unit*.04f,-unit*.4f),unit*.025f,StrokeCap.Round)
                drawOval(Color(0xFF77B98C),Offset(unit*.015f,-unit*.43f),Size(unit*.19f,unit*.1f))
                drawOval(if(character=="pip") Gold else Color(0xFF9BD2B0),Offset(-unit*.25f,-unit*.28f),Size(unit*.5f,unit*.55f))
                drawLine(Ink,Offset(-unit*.12f,unit*.21f),Offset(-unit*.15f,unit*.29f),unit*.035f,StrokeCap.Round)
                drawLine(Ink,Offset(unit*.12f,unit*.21f),Offset(unit*.15f,unit*.29f),unit*.035f,StrokeCap.Round)
                val arm=if(celebrating) -unit*.13f else unit*.08f
                drawLine(Ink,Offset(-unit*.23f,0f),Offset(-unit*.33f,arm),unit*.025f,StrokeCap.Round)
                drawLine(Ink,Offset(unit*.23f,0f),Offset(unit*.33f,arm),unit*.025f,StrokeCap.Round)
                val blink=if(seconds>5.8f) .12f else 1f
                for(x in listOf(-unit*.09f,unit*.09f)) {
                    drawOval(Ink,Offset(x-unit*.019f,-unit*.045f),Size(unit*.038f,unit*.057f*blink))
                    drawCircle(Coral,unit*.035f,Offset(x*1.55f,unit*.045f))
                }
                if(speaking) drawOval(Ink,Offset(-unit*.04f,unit*.045f),Size(unit*.08f,unit*(.035f+.045f*abs(sin(seconds*14)))))
                else drawArc(Ink,0f,180f,false,Offset(-unit*.065f,unit*.025f),Size(unit*.13f,unit*.1f),style=Stroke(unit*.015f,cap=StrokeCap.Round))
            }
        }
        if(celebrating) repeat(28) { i ->
            val p=((seconds/6.283185f)+i/28f)%1f
            val x=(i*73.7f%100f)/100f*size.width
            val y=p*size.height
            rotate(i*31f+seconds*30f,Offset(x,y)) { drawOval(TileColors[i%TileColors.size].copy(alpha=.5f+.5f*Motion.celebration(p)),Offset(x,y),Size(unit*.025f,unit*.055f)) }
        }
        repeat(3) { i -> val x=cx+unit*(i-1)*.37f; drawCircle(Color.White.copy(alpha=.7f),unit*.025f,Offset(x,unit*.15f+sin(seconds+i)*unit*.03f)) }
    }
}

@Composable
private fun ParentGate(onDismiss:()->Unit,onSuccess:()->Unit) {
    val a=remember { Random.nextInt(12,24) }; val b=remember { Random.nextInt(8,17) }
    var answer by remember { mutableStateOf("") }; var incorrect by remember { mutableStateOf(false) }
    AlertDialog(onDismissRequest=onDismiss,title={Text("Grown-ups only")},text={Column { Text("Please solve $a + $b to open settings."); OutlinedTextField(value=answer,onValueChange={answer=it},label={Text("Answer")},singleLine=true,modifier=Modifier.padding(top=12.dp)); if(incorrect) Text("Try again",color=MaterialTheme.colorScheme.error) }},confirmButton={TextButton(onClick={if(answer.toIntOrNull()==a+b) onSuccess() else incorrect=true}){Text("Open settings")}},dismissButton={TextButton(onClick=onDismiss){Text("Back to play")}})
}

@Composable
private fun ParentSettings(model:GameViewModel,onDismiss:()->Unit) {
    val status by model.voice.status.collectAsStateWithLifecycle()
    var reset by remember { mutableStateOf(false) }
    AlertDialog(onDismissRequest=onDismiss,title={Text("The grown-up corner")},text={Column(Modifier.verticalScroll(rememberScrollState())) {
        Text("Sound volume · ${(model.volume*100).roundToInt()}%",fontWeight=FontWeight.Bold)
        Slider(value=model.volume,onValueChange=model::changeVolume)
        Text(status,fontSize=13.sp)
        TextButton(onClick={model.voice.speak("Hello! Let's grow a word together.")}){Text("Test voice")}
        Row(verticalAlignment=Alignment.CenterVertically) { Text("Try letter sounds",Modifier.weight(1f)); Switch(checked=model.phonetic,onCheckedChange=model::changePhonetic) }
        Text("Off: letter names. On: approximate phonics using your phone's voice. Pronunciation varies by voice; adult guidance is recommended.",fontSize=12.sp)
        Text("108 words · 3 gardens\nAll progress stays on this phone. No ads, purchases, accounts or tracking. English speech needs an installed offline voice.",Modifier.padding(top=16.dp),fontSize=13.sp)
        TextButton(onClick={reset=true}){Text("Reset learning progress")}
        if(reset) Row { TextButton(onClick={model.resetProgress();reset=false}){Text("Confirm reset")}; TextButton(onClick={reset=false}){Text("Keep progress")} }
    }},confirmButton={TextButton(onClick={model.voice.stop();onDismiss()}){Text("Done")}})
}
