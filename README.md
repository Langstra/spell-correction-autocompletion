# Domain Specific Spell Correction
This is a tool aiding in the research into domain specific spell correction. The tool does spell correction based on a large dataset. There are 2 datasets, one containing general English words and one containing words from a specific domain.

## Getting started
```
git clone git@github.com:Langstra/spell-correction-autocompletion.git
cd spell-correction-autocompletion
mvn clean install
java -cp target/noisy_channel-1.0.0.jar corrector.Spelling 
```

