/*
 * Corbit, a text analyzer
 * 
 * Copyright (c) 2010-2012, Jun Hatori
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the names of the authors nor the names of its contributors
 *       may be used to endorse or promote products derived from this
 *       software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package corbit.tagdep;

import corbit.commons.io.Console;
import corbit.commons.util.GlobalConf;
import corbit.tagdep.dict.CTBTagDictionary;
import corbit.tagdep.dict.TagDictionary;
import corbit.tagdep.io.CTBReader;
import corbit.tagdep.io.CoNLLReader;
import corbit.tagdep.io.InputFormat;
import corbit.tagdep.io.ParseReader;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

class Program {

    static void initializeExperimentalConf(GlobalConf conf) {
    }

    static boolean evalExperimentalOpt(GlobalConf conf, int i, List<String> lArgs) {
        return false;
    }

    static boolean evalCommonOpt(SRParser parser, int i, List<String> lArgs) throws IOException {
        switch (lArgs.get(i)) {
            case "--no-parse":
                lArgs.remove(i);
                parser.m_bParse = false;
                parser.m_bUseSyntax = false;
                break;
            case "--ft":
            case "--feature-type":
                lArgs.remove(i);
                parser.setFeatureHandler(Integer.parseInt(lArgs.get(i)));
                lArgs.remove(i);
                break;
            case "--input-format":
                lArgs.remove(i);
                String sFormat = lArgs.get(i).toLowerCase();
                switch (sFormat) {
                    case "ctb":
                        parser.m_iInputFormat = InputFormat.CTB;
                        break;
                    case "malt":
                        parser.m_iInputFormat = InputFormat.Malt;
                        break;
                    case "conll":
                        parser.m_iInputFormat = InputFormat.CoNLL;
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown format: " + sFormat);
                }
                break;
            case "--gold-pos":
                lArgs.remove(i);
                parser.m_bUseGoldPos = true;
                parser.m_bAssignPosFollowsShift = false;
                break;
            case "--assign-gold":
                lArgs.remove(i);
                parser.m_bAssignGoldPos = true;
                break;
            case "--no-pos-feature":
                lArgs.remove(i);
                parser.m_bUseTagFeature = false;
                break;
            case "-b":
            case "--beam-size":
                lArgs.remove(i);
                parser.m_iBeam = Integer.parseInt(lArgs.get(i));
                lArgs.remove(i);
                break;
            case "--debug":
                lArgs.remove(i);
                parser.m_bDebug = true;
                break;
            case "--no-compress":
                lArgs.remove(i);
                parser.m_bCompressedModel = false;
                break;
            case "--show-stats":
                lArgs.remove(i);
                parser.m_bShowStats = true;
                break;
            case "--parallel":
                lArgs.remove(i);
                parser.m_iParallel = Integer.parseInt(lArgs.get(i));
                lArgs.remove(i);
                break;
            default:
                return false;
        }
        return true;
    }

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        Console.open();
        SRParser parser = new SRParser();
        List<String> lArgs = new LinkedList<>(java.util.Arrays.asList(args));

        GlobalConf conf = GlobalConf.getInstance();
        initializeExperimentalConf(conf);

        if (lArgs.size() < 1) {
            usage();
        } else if (lArgs.get(0).equals("Train")) {
            if (lArgs.size() < 5) {
                usage();
            }

            String sDictFile = null;
            int iDictThreshold = 0;
            boolean bResume = false;

            for (int i = 1; i < lArgs.size();) {
                if (lArgs.get(i).equals("--thres")) {
                    lArgs.remove(i);
                    iDictThreshold = Integer.parseInt(lArgs.get(i));
                    lArgs.remove(i);
                } else if (lArgs.get(i).equals("--dict")) {
                    lArgs.remove(i);
                    sDictFile = lArgs.get(i);
                    lArgs.remove(i);
                } else if (lArgs.get(i).equals("--load")) {
                    lArgs.remove(i);
                    parser.loadModel(lArgs.get(i));
                    lArgs.remove(i);
                } else if (lArgs.get(i).equals("--ft")) {
                    lArgs.remove(i);
                    int iType = Integer.parseInt(lArgs.get(i));
                    lArgs.remove(i);
                    parser.setFeatureHandler(iType);
                } else if (lArgs.get(i).equals("--resume")) {
                    lArgs.remove(i);
                    bResume = true;
                } else if (lArgs.get(i).equals("--margin")) {
                    lArgs.remove(i);
                    parser.m_dMargin = Double.parseDouble(lArgs.get(i));
                    lArgs.remove(i);
                } else if (lArgs.get(i).equals("--depth")) {
                    lArgs.remove(i);
                    parser.m_iDepth = Integer.parseInt(lArgs.get(i));
                    lArgs.remove(i);
                } else if (lArgs.get(i).equals("--no-early")) {
                    lArgs.remove(i);
                    parser.m_bEarlyUpdate = false;
                } else if (lArgs.get(i).equals("--no-average")) {
                    lArgs.remove(i);
                    parser.m_bAveraged = false;
                } else if (lArgs.get(i).equals("--batch")) {
                    lArgs.remove(i);
                    parser.m_bLoadOnMemory = false;
                } else if (lArgs.get(i).equals("--no-dp")) {
                    lArgs.remove(i);
                    parser.m_bDP = false;
                } else if (lArgs.get(i).equals("--save-each")) {
                    lArgs.remove(i);
                    parser.m_bSaveEach = true;
                } else if (lArgs.get(i).equals("--no-delay")) {
                    lArgs.remove(i);
                    parser.m_bEvalDelay = false;
                } else if (lArgs.get(i).equals("--no-syntax")) {
                    lArgs.remove(i);
                    parser.m_bUseSyntax = false;
                } else if (lArgs.get(i).equals("--no-rfs")) {
                    lArgs.remove(i);
                    parser.m_bAssignPosFollowsShift = false;
                } else if (lArgs.get(i).equals("--no-strict-stop")) {
                    lArgs.remove(i);
                    parser.m_bStrictStop = false;
                } else if (lArgs.get(i).equals("--no-lookahead")) {
                    lArgs.remove(i);
                    parser.m_bUseLookAhead = false;
                } else if (lArgs.get(i).equals("--use-trie")) {
                    lArgs.remove(i);
                    parser.m_bUseTrie = true;
                    parser.setUseTrie();
                } else if (lArgs.get(i).equals("--rebuild-vocab")) {
                    lArgs.remove(i);
                    parser.m_bRebuildVocab = true;
                } else if (lArgs.get(i).equals("--no-shuffle")) {
                    lArgs.remove(i);
                    parser.m_bShuffle = false;
                } else if (lArgs.get(i).equals("--aggressive-parallel")) {
                    lArgs.remove(i);
                    parser.m_bAggressiveParallel = true;
                } else if (lArgs.get(i).equals("--closed-tags")) {
                    lArgs.remove(i);
                    parser.m_bUseClosedTags = true;
                    parser.setUseClosedTags(true);
                } else if (evalCommonOpt(parser, i, lArgs)); 
                  else if (evalExperimentalOpt(conf, i, lArgs)); 
                  else if (lArgs.get(i).startsWith("-")) {
                    System.err.println("Unknown option: " + lArgs.get(i));
                    System.exit(-1);
                } else {
                    ++i;
                }
            }
            if (lArgs.size() < 5) {
                usage();
            } else {
                String sModelFile = lArgs.get(4);
                String sLastModelFile = sModelFile + ".last";
                if ((new File(sModelFile)).exists()) {
                    if (bResume) {
                        if ((new File(sLastModelFile)).exists()) {
                            parser.loadModel(sLastModelFile);
                        } else {
                            parser.loadModel(sModelFile);
                        }
                    } else {
                        System.err.println(sModelFile + " already exists. Will be overwritten.");
                    }
                }
                if (sDictFile != null) {
                    parser.loadDictFromFile(sDictFile, iDictThreshold);
                }
                if (!parser.m_bParse) {
                    parser.setFeatureHandler(SRParser.FeatureType.ZC08);
                }
                parser.train(lArgs.get(1), lArgs.get(2), sModelFile, Integer.parseInt(lArgs.get(3)));
            }
        } else if (lArgs.get(0).equals("Run")) {
            if (lArgs.size() < 2) {
                usage();
            } else {
                parser.loadModel(lArgs.get(1));

                boolean bPrintParams = false;
                for (int i = 1; i < lArgs.size();) {
                    if (lArgs.get(i).equals("--print-params")) {
                        lArgs.remove(i);
                        bPrintParams = true;
                    } 
                    else if (evalCommonOpt(parser, i, lArgs)); 
                    else if (evalExperimentalOpt(conf, i, lArgs)); 
                    else if (lArgs.get(i).startsWith("-")) {
                        System.err.println("Unknown option: " + lArgs.get(i));
                        System.exit(-1);
                    } else {
                        ++i;
                    }
                }
                if (bPrintParams) {
                    parser.printProperties();
                }
                System.err.println("\nReady.");
                parser.run();
            }
        } else if (lArgs.get(0).equals("Test")) {
            if (lArgs.size() < 3) {
                usage();
            } else {
                parser.loadModel(lArgs.get(1));

                String sParseFile = null;
                String sRefFile = null;
                boolean bPrintParams = false;
                for (int i = 1; i < lArgs.size();) {
                    if (lArgs.get(i).equals("--save-pos")) {
                        lArgs.remove(i);
                        sParseFile = lArgs.get(i);
                        lArgs.remove(i);
                        parser.m_bSaveOnlyPos = true;
                    } else if (lArgs.get(i).equals("--save-parse")) {
                        lArgs.remove(i);
                        sParseFile = lArgs.get(i);
                        lArgs.remove(i);
                        parser.m_bSaveOnlyPos = false;
                    } else if (lArgs.get(i).equals("--print-params")) {
                        lArgs.remove(i);
                        bPrintParams = true;
                    } else if (lArgs.get(i).equals("--gold-ref")) {
                        lArgs.remove(i);
                        sRefFile = lArgs.get(i);
                        lArgs.remove(i);
                    } else if (evalCommonOpt(parser, i, lArgs)); 
                      else if (evalExperimentalOpt(conf, i, lArgs)); 
                      else if (lArgs.get(i).startsWith("-")) {
                        System.err.println("Unknown option: " + lArgs.get(i));
                        System.exit(-1);
                    } else {
                        ++i;
                    }
                }
                if (bPrintParams) {
                    parser.printProperties();
                }
                parser.test(lArgs.get(2), sRefFile, sParseFile);
            }
        } else if (lArgs.get(0).equals("CreateDict")) {
            if (lArgs.size() < 3) {
                usage();
            }
            TagDictionary dict = new CTBTagDictionary(true);
            if (lArgs.get(3).equals("--input-format")) {
                String sFormat = lArgs.get(4).toLowerCase();
                switch (sFormat) {
                    case "ctb":
                        dict.createCountDict(
                                new CTBReader(lArgs.get(1)), 
                                lArgs.get(2));
                        break;
                    case "conll":
                        dict.createCountDict(
                                new CoNLLReader(lArgs.get(1)), 
                                lArgs.get(2));
                        break;
                }
            }
        } else if (lArgs.get(0).equals("MaltToDep")) {
            if (lArgs.size() < 3) {
                usage();
            }
            ParseReader.maltToDep(lArgs.get(1), lArgs.get(2));
        } else if (lArgs.get(0).equals("CtbToPlain")) {
            if (lArgs.size() < 3) {
                usage();
            }
            ParseReader.ctbToPlain(lArgs.get(1), lArgs.get(2));
        } else if (lArgs.get(0).equals("ConllToPlain")) {
            if (lArgs.size() < 3) {
                usage();
            }
            ParseReader.conllToPlain(lArgs.get(1), lArgs.get(2));
        } else if (lArgs.get(0).equals("EvalPos")) {
            if (lArgs.size() < 3) {
                usage();
            }
            TagDictionary dict = new CTBTagDictionary(true);
            ParseReader.evalPos(dict, lArgs.get(1), lArgs.get(2));
        } else {
            usage();
        }

        Console.close();
    }

    static void usage() {
//		System.err.println("Corbit, a Chinese text analyzer  -  Copyright (c) 2010-2012, Jun Hatori");
        System.err.println("Usage: ./corbit.sh <d|pd> <Run|Train|Test> (arguments..)");
        System.err.println();
        System.err.println("Run (model-file-to-load) [options..] < (input-file) > (output-file)");
        System.err.println("  --print-params       print the list of model and program parameters");
        System.err.println();
        System.err.println("Train (train-file) (dev-file) (#iteration) (model-file-to-save) [options..]");
        System.err.println("  --beam-size,-b (int) set the beam size");
        System.err.println("  --dict (file)        use the file (made using CreateDict command) as a tag dictionary");
        System.err.println("  --thres (int)        set the threshold frequency to use the set of tags in the dictionary");
        System.err.println("  --input-format <malt|ctb>");
        System.err.println("                       specify the format of input data");
        System.err.println("  --ft <1|2>           1 (default): use feature templates from Huang and Sagae (2010)");
        System.err.println("    feature-type <1|2> 2: use additional feature templates from Zhang and Nivre (2011)");
        System.err.println("  --rebuild-vocab      remove unnecessary items in the vocabulary at the end of each iteration");
        System.err.println("  --use-trie           use Patricia trie to store the feature index (default: HashMap)");
        System.err.println("  --load (file)        load a model file and continue training");
        System.err.println("  --resume             resume training if a temporary file is availale");
        System.err.println("  --no-shuffle         disable shuffling of training instances");
        System.err.println("  --no-closed-tags     do not use closed-set tags information");
        System.err.println("  --no-early           disable early update");
        System.err.println("  --no-parse           disable dependency parsing (i.e. POS tagging only)");
        System.err.println("  --no-average         disable averaged perceptron (i.e. non-averaged perceptron)");
        System.err.println("  --no-dp              disable dynamic programming as described in Huang and Sagae (2010)");
        System.err.println("  --no-delay           disable delayed features");
        System.err.println("  --no-syntax          disable syntactic featuers");
        System.err.println("  --assign-gold        use gold POS tags for shift actions");
        System.err.println("                       simulate dependency parsing with gold POS tags when used with --gold-pos");
        System.err.println("  --gold-pos           use gold POS tags for look-ahead features");
        System.err.println();
        System.err.println("  (options below can also be used for Test command)");
        System.err.println("  --parallel (int)     specified the number of CPUs to use for decoding");
        System.err.println("  --print-params       print the list of model and program parameters");
        System.err.println("  --show-stats         show statistics during decoding");
        System.err.println("  --debug              print outputs");
        System.err.println();
        System.err.println("Test (model-file-to-load) (test-file) [options..]");
        System.err.println("  --save-pos (file)    save the output POS tags to the file");
        System.err.println("  --save-parse (file)  save the output POS tags and parse trees to the file");
        System.err.println();
//		System.err.println("CreateDict (target-file) (dict-file-to-save)");
    }
}
