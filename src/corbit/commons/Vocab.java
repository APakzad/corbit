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
package corbit.commons;

import corbit.commons.io.Console;
import corbit.commons.ml.AveragedWeight;
import corbit.commons.ml.WeightVector;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.ardverk.collection.PatriciaTrie;
import org.ardverk.collection.StringKeyAnalyzer;

public class Vocab {

    Map<String, Integer> m_index;
    List<String> m_rindex;
    int m_size = 0;

    public Vocab() {
        m_index = new HashMap<>();
        m_rindex = new ArrayList<>();
    }

    public Vocab(String sFile) {
        this();
        try {
            load(sFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Vocab(BufferedReader br) {
        this();
        try {
            load(br);
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    public Set<String> getKeys() {
        return m_index.keySet();
    }

    /**
     * rebuild the entire vocabulary with unused items removed should be
     * effective for reducing memory use
     *
     * @param w
     */
    public void rebuild(AveragedWeight w) {
        Map<String, Integer> oldIndex = m_index;
        m_index = (m_index instanceof HashMap)
                ? new HashMap<String, Integer>()
                : new PatriciaTrie<String, Integer>(StringKeyAnalyzer.INSTANCE);
        m_rindex.clear();
        WeightVector ww = new WeightVector(w);
        WeightVector wa = new WeightVector(w.getAverageWeight());
        WeightVector wwNew = w;
        WeightVector waNew = w.getAverageWeight();
        wwNew.clear();
        waNew.clear();

        int iNewSize = 0;

        for (Entry<String, Integer> e : oldIndex.entrySet()) {
            String sKey = e.getKey();
            int oldidx = e.getValue();

            if (ww.get(oldidx) != 0.0d || wa.get(oldidx) != 0.0d) {
                int newidx = iNewSize++;
                m_index.put(sKey, newidx);
                m_rindex.add(sKey);
                wwNew.put(newidx, ww.get(oldidx));
                waNew.put(newidx, wa.get(oldidx));
            }
        }
        m_size = iNewSize;
        assert (m_size == m_index.size());

        Console.writeLine(String.format("Vocab and AveragedWeight rebuilt: size %d => %d.", oldIndex.size(), m_rindex.size()));
    }

    public void setUseTrie() {
        if (m_index instanceof HashMap) {
            Console.writeLine("HashMap converted to PatriciaTrie.");
            m_index = new PatriciaTrie<>(StringKeyAnalyzer.INSTANCE, m_index);
        }
    }

    public void clear() {
        m_index.clear();
        m_rindex.clear();
        m_size = 0;
    }

    public boolean contains(String s) {
        return m_index.containsKey(s);
    }

    public int get(String s) {
        return m_index.get(s);
    }

    public Integer getBoxed(String s) {
        return m_index.get(s);
    }

    public String get(int i) {
        return m_rindex.get(i);
    }

    public int getIndex(String s) {
        assert (s != null);
        Integer i;
        if ((i = m_index.get(s)) != null) {
            return i.intValue();
        } else {
            m_index.put(s, m_size);
            m_rindex.add(s);
            return m_size++;
        }
    }

    public void save(String sFile) throws FileNotFoundException, UnsupportedEncodingException {
        try (PrintWriter sw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(sFile), "UTF-8"))) {
            save(sw);
        }
    }

    public void save(PrintWriter sw) {
        int n = m_rindex.size();
        sw.println(n);
        for (int i = 0; i < n; ++i) {
            sw.println(m_rindex.get(i));
        }
        Console.writeLine(n + " vocabulary entries saved.");
    }

    public final void load(String sFile) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(sFile), "UTF-8"))) {
            int numWords = 0;
            String sLine;
            while ((sLine = br.readLine()) != null) {
                String[] fields = Normalizer.normalize(sLine, Normalizer.Form.NFKC).split("[ \t]+");
                if (!m_index.containsKey(fields[0])) {
                    m_rindex.add(fields[0]);
                    m_index.put(fields[0], numWords++);
                }
            }
            m_size = numWords;
        }
        System.err.println(m_size + " dictionary entries loaded from " + sFile + ".");
    }

    public final void load(BufferedReader sr) throws NumberFormatException, IOException {
        m_size = Integer.parseInt(sr.readLine());
        for (int i = 0; i < m_size; ++i) {
            String s = sr.readLine();
            assert (!m_index.containsKey(s));
            m_rindex.add(s);
            m_index.put(s, i);
        }
        System.err.println(m_size + " vocabulary entries loaded from the model.");
    }
}