"""Original learning pack; re-run to regenerate the bundled JSON."""
import json
from pathlib import Path
levels = [
'''a|A little seed can grow.
i|I can sing a song.
am|I am ready to play.
an|An ant walks on a leaf.
as|Hop as high as you can.
at|Look at the moon.
be|Be kind to a friend.
by|Sit by the tree.
do|What do you see?
go|We go to the park.
he|He has a red hat.
if|We can play if it is sunny.
in|The seed is in a pot.
is|The sun is warm.
it|It is time to play.
me|Can you help me?
my|This is my book.
no|There is no rain today.
on|A bird sits on a branch.
or|Would you like milk or water?
so|The flower is so pretty.
to|Let us go to the garden.
up|The balloon floats up.
us|Come and play with us.
we|We can read together.
cat|The cat takes a nap.
dog|The dog runs to the gate.
sun|The sun warms the garden.
map|We look at a map.
hat|My hat keeps me cool.
cup|The cup is full of water.
bed|The bear rests in bed.
red|I found a red leaf.
big|The big tree gives us shade.
run|We run across the grass.
sit|Sit beside your friend.
''' ,
'''and|You and I can read.
are|The birds are singing.
but|I am small but I can help.
can|I can build a tower.
did|What did you find?
for|This flower is for you.
get|Let us get a book.
had|We had fun in the garden.
has|The bird has a nest.
her|She carries her book.
him|I gave him a flower.
his|He puts on his boots.
not|The little seed is not a stone.
one|One bird sits in the tree.
our|This is our garden.
out|The chick comes out of its egg.
see|I see a butterfly.
she|She can hop on one foot.
the|The moon shines at night.
they|They plant a seed together.
too|I can help too.
two|Two ducks swim in the pond.
was|The tiny seed was asleep.
way|This path is the way home.
who|Who is behind the tree?
yes|Yes, we can play together.
you|You can try again.
with|I share my book with you.
this|This is a soft leaf.
that|Look at that bright star.
fish|The fish swims in the pond.
bird|A bird sings in the tree.
ball|We roll the ball slowly.
book|Let us read a book.
tree|A tree grows from a seed.
moon|The moon looks round tonight.
''' ,
'''all|We all help in the garden.
away|The butterfly flies away.
blue|The sky is blue today.
come|Come and see the new flower.
down|A leaf floats down.
each|Each seed needs water.
find|Can you find the little snail?
from|A sprout grows from the seed.
give|I give the plant some water.
good|It is a good day to play.
have|We have a new book.
help|I can help my friend.
here|Put the little pot here.
jump|The frog can jump far.
like|I like to read with you.
little|A little snail rests on a leaf.
look|Look at the tall sunflower.
make|We make a cozy nest.
many|Many stars shine at night.
more|The plant needs more water.
new|A new leaf is growing.
now|Now we can play a game.
play|We play beside the pond.
said|She said hello to the bird.
some|Some clouds look like sheep.
there|There is a frog on the rock.
three|Three seeds sleep in the soil.
under|A beetle hides under a leaf.
want|I want to read a story.
water|Water helps the flowers grow.
where|Where is the little bird?
what|What can we build today?
when|We rest when we feel tired.
will|The seed will grow tall.
yellow|A yellow flower opens today.
green|The green frog sits by the pond.
''']
words=[]
for level, rows in enumerate(levels, 1):
 for row in rows.strip().splitlines():
  word,sentence=row.split('|')
  words.append(dict(id=f'core-{word}',text=word,difficulty=level,pronunciation=word if word!='i' else 'I',sentence=sentence,animation=dict(bounce=1.0+level*.12,celebration='leaves'),character='pip' if len(words)%2==0 else 'moss'))
path=Path(__file__).resolve().parents[1]/'app/src/main/assets/content/core.json'
path.write_text(json.dumps(dict(schemaVersion=1,packId='garden-words',title='Garden words',words=words),indent=2)+'\n')
print(f'Created {len(words)} original word entries')
