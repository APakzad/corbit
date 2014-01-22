package corbit.tagdep.io;

import corbit.commons.io.Console;
import corbit.commons.io.FileEnum;
import corbit.commons.util.Statics;
import corbit.tagdep.word.DepTree;
import corbit.tagdep.word.DepTreeSentence;

/**
 *
 * @author Mojtaba Khallash
 */
public class CoNLLReader extends ParseReader {

    private final String m_sFile;

    public CoNLLReader(String sFile) {
        this.m_sFile = sFile;
    }

    @Override
    protected void iterate() throws InterruptedException {
        int iSentence = 0;
        FileEnum fe = new FileEnum(m_sFile);
//        Set<String> posSet = CTBTagDictionary.copyTagSet();

        try {
            int j = 0;
            DepTreeSentence s = new DepTreeSentence();
            for (String l : fe) {
                l = Statics.trimSpecial(l);
                if (l.length() == 0) {
                    if (s.size() > 0) {
                        yieldReturn(s);
                        ++iSentence;
                    }
                    s = new DepTreeSentence();
                    j = 0;
                    continue;
                }
                String[] ss = l.split("\t");
                int iIndex = Integer.parseInt(ss[0]) - 1;
                String sForm = ss[1];
                String lemma = ss[2];
                String cpos = ss[3];
//                if (!posSet.contains(cpos)) {
//                    Console.writeLine("Unknown POS: " + cpos);
////                        break;
//                }
                String fpos = ss[4];
                String feat = ss[5];
                int iHead = Integer.parseInt(ss[6]) - 1;
                String dependency = ss[7];
                
                int count = iHead + 1 - s.size();
                for (int i = 0; i < count; ++i) {
                    s.add(new DepTree());
                }
                
                if (j >= s.size())
                    s.add(new DepTree());

                DepTree dw = s.get(j);
                dw.sent = s;
                dw.index = iIndex;
                dw.form = sForm;
                dw.pos = cpos;
                dw.head = iHead;

                if (j != iIndex) {
                    Console.writeLine(String.format("Illegal index at line %d. Skipping.", iSentence));
                    s = null;
                    break;
                }

                if (iHead >= 0) {
                    s.get(iHead).children.add(dw);
                }
                j++;
            }
        } catch (NumberFormatException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            fe.shutdown();
        }
    }
}