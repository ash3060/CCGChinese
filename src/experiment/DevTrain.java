/***************************  LICENSE  *******************************
* This file is part of UBL.
*
* UBL is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* UBL is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with UBL.  If not, see <http://www.gnu.org/licenses/>.
***********************************************************************/
package experiment;

import java.io.File;
import java.util.*;

import learn.*;
import lambda.*;
import parser.*;
import KB.*;

public class DevTrain {

	public static void trainAndTest(String trainFile, String testFile, testResult d_res, testResult t_res, int beam) {
		PType.addTypesFromFile("data/types");
		Lang.loadLangFromFile("data/relations");

		DataSet train = new DataSet(trainFile);
		System.out.println("Train Size: "+train.size());
		System.out.println();

		DataSet test = new DataSet(testFile);
		System.out.println("Test Size: "+test.size());
		System.out.println();

	    //Initialize parameters
		//String giza_probs = "data/"+language+"/run-"+runNum+"/fold-"+splitNum+"/w-c.giza_probs";
		//LexiconFeatSet.loadCoOccCounts(giza_probs);

		//fixed np lexicon
		Lexicon fixed = new Lexicon();
		
		//extract NP lexicon from KB directly
		//extractNP extract = new extractNP();
		//extract.extract2lex(fixed);
		
		//load NP lexicon from file
		String npLexFileName = "data/np_lexicon";
		fixed.addEntriesFromFile(npLexFileName,true);

		Train.EPOCHS = 5;
		//Train.EPOCHS = epo;
		Train.alpha_0 = 1.0;
		Train.c = 0.00001;
		Train.maxSentLen=50;

		LexiconFeatSet.initWeightMultiplier = 10.0;
		LexiconFeatSet.initLexWeight = 10.0;
		//Parser.pruneN=200;
		Parser.pruneN = beam;

		System.out.println("alpha_0 = "+Train.alpha_0);
		System.out.println("C = "+Train.c);
		System.out.println("initialMultiplier = "+LexiconFeatSet.initWeightMultiplier);
		System.out.println("NP init = "+LexiconFeatSet.initLexWeight);
		System.out.println("Parser beam  = "+Parser.pruneN);
		System.out.println();

		Parser p = new Parser(fixed);
		p.makeFeatures();
		//p.returnLex().printLexiconWithWeightsToFile();

		System.out.println("Start train");
		Train t = new Train();
		t.addTrainSet(train);
		t.addTestSet(test);

		Train.verbose = false;
		
		System.out.println("Start training...");
		t.stocGradTrain(p,false);
		
		System.out.println("Print lexicon...");
		p.returnLex().printLexiconWithWeightsToFile();
		
		if (d_res != null) {
			System.out.println("Start testing on train set...");
			t.test(p,Train.pruneLex, t.getTrainData(0), d_res);
		}
		
		if (t_res != null){
			System.out.println("Start testing on test set...");
			t.test(p,Train.pruneLex, t.getTestData(0), t_res);
		}
		else {
			System.out.println("Start testing on test set...");
			t.test(p,Train.pruneLex, t_res);
		}

		
		return;

	}
	
	
	public static void main(String[] args){
		
		//trainAndTest("data/train", "data/train", null, null);

		return;
	}
}


