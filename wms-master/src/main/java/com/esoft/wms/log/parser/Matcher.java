/**
 * Copyright (C) 2012-2013 The named-regexp Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.esoft.wms.log.parser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * An engine that performs match operations on a character sequence by
 * interpreting a {@link Pattern}. This is a wrapper for {@link java.util.regex.Matcher}.
 *
 * @since 0.1.9
 */
public class Matcher implements MatchResult {

    private java.util.regex.Matcher matcher;
    private Pattern parentPattern;

    Matcher(Pattern parentPattern, java.util.regex.MatchResult matcher) {
        this.parentPattern = parentPattern;
        this.matcher = (java.util.regex.Matcher) matcher;
    }

    Matcher(Pattern parentPattern, CharSequence input) {
        this.parentPattern = parentPattern;
        this.matcher = parentPattern.pattern().matcher(input);
    }

    /**
     * Returns the pattern that is interpreted by this matcher.
     *
     * @return the pattern
     */
    public java.util.regex.Pattern standardPattern() {
        return matcher.pattern();
    }

    /**
     * Returns the named pattern that is interpreted by this matcher.
     *
     * @return the pattern
     */
    public Pattern namedPattern() {
        return parentPattern;
    }

    /**
     * Changes the Pattern that this Matcher uses to find matches with
     *
     * @param newPattern the new pattern
     * @return this Matcher
     */
    public Matcher usePattern(Pattern newPattern) {
        if (newPattern == null) {
            throw new IllegalArgumentException("newPattern cannot be null");
        }
        this.parentPattern = newPattern;
        matcher.usePattern(newPattern.pattern());
        return this;
    }

    /**
     * Resets this matcher
     *
     * @return this Matcher
     */
    public Matcher reset() {
        matcher.reset();
        return this;
    }

    /**
     * Resets this matcher with a new input sequence
     *
     * <p>Resetting a matcher discards all of its explicit state information
     * and sets its append position to zero. The matcher's region is set to
     * the default region, which is its entire character sequence. The
     * anchoring and transparency of this matcher's region boundaries are
     * unaffected</p>
     *
     * @param input The new input character sequence
     * @return this Matcher
     */
    public Matcher reset(CharSequence input) {
        matcher.reset(input);
        return this;
    }

    /**
     * Attempts to match the entire region against the pattern.
     *
     * <p>If the match succeeds then more information can be obtained via
     * the start, end, and group methods.</p>
     *
     * @return <code>true</code> if, and only if, the entire region sequence
     * matches this matcher's pattern
     */
    public boolean matches() {
        return matcher.matches();
    }

    /**
     * Returns the match state of this matcher as a NamedMatchResult. The result
     * is unaffected by subsequent operations performed upon this matcher.
     *
     * @return a NamedMatchResult with the state of this matcher
     */
    public MatchResult toMatchResult() {
        return new Matcher(this.parentPattern, matcher.toMatchResult());
    }

    /**
     * Attempts to find the next subsequence of the input sequence that matches
     * the pattern.
     *
     * <p>This method starts at the beginning of this matcher's region, or,
     * if a previous invocation of the method was successful and the matcher
     * has not since been reset, at the first character not matched by the
     * previous match.</p>
     *
     * <p>If the match succeeds then more information can be obtained via the
     * start, end, and group methods.</p>
     *
     * @return
     */
    public boolean find() {
        return matcher.find();
    }

    /**
     * Resets this matcher and then attempts to find the next subsequence of
     * the input sequence that matches the pattern, starting at the specified
     * index.
     *
     * <p>If the match succeeds then more information can be obtained via the
     * start, end, and group methods, and subsequent invocations of the find()
     * method will start at the first character not matched by this match.</p>
     * @param start the starting index
     * @return <code>true</code> if, and only if, a subsequence of the input
     * sequence starting at the given index matches this matcher's pattern
     * @throws IndexOutOfBoundsException If start is less than zero or if start
     * is greater than the length of the input sequence.
     */
    public boolean find(int start) {
        return matcher.find(start);
    }

    /**
     * Attempts to match the input sequence, starting at the beginning of the
     * region, against the pattern.
     *
     * <p>Like the matches method, this method always starts at the beginning
     * of the region; unlike that method, it does not require that the entire
     * region be matched.</p>
     *
     * <p>If the match succeeds then more information can be obtained via the
     * start, end, and group methods.</p>
     *
     * @return <code>true</code> if, and only if, a prefix of the input sequence
     * matches this matcher's pattern
     */
    public boolean lookingAt() {
        return matcher.lookingAt();
    }

    /**
     * Implements a non-terminal append-and-replace step.
     *
     * @param sb The target string buffer
     * @param replacement The replacement string
     * @return The target string buffer
     */
    public Matcher appendReplacement(StringBuffer sb, String replacement) {
        matcher.appendReplacement(sb, parentPattern.replaceProperties(replacement));
        return this;
    }

    /**
     * Implements a terminal append-and-replace step.
     *
     * @param sb The target string buffer
     * @return The target string buffer
     */
    public StringBuffer appendTail(StringBuffer sb) {
        return matcher.appendTail(sb);
    }

    /**
     * Returns the input subsequence matched by the previous match.
     *
     * @return The (possibly empty) subsequence matched by the previous match,
     * in string form
     */
    @Override
    public String group() {
        return matcher.group();
    }

    /**
     * Returns the input subsequence captured by the given group during the
     * previous match operation.
     *
     * @param group The index of a capturing group in this matcher's pattern
     * @return the subsequence
     * @throws IllegalStateException If no match has yet been attempted, or
     * if the previous match operation failed
     */
    @Override
    public String group(int group) {
        return matcher.group(group);
    }

    /**
     * Returns the number of capturing groups in this matcher's pattern.
     *
     * @return The number of capturing groups in this matcher's pattern
     */
    @Override
    public int groupCount() {
        return matcher.groupCount();
    }

    /**
     * Gets a list of the matches in the order in which they occur
     * in a matching input string
     *
     * @return the matches
     */
    @Override
    public List<String> orderedGroups() {
        int groupCount = groupCount();
        List<String> groups = new ArrayList<String>(groupCount);
        for (int i = 1; i <= groupCount; i++) {
            groups.add(group(i));
        }
        return groups;
    }

    /**
     * Returns the input subsequence captured by the named group during
     * the previous match operation.
     *
     * @param groupName name of the capture group
     * @return the subsequence
     * @throws IndexOutOfBoundsException if group name not found
     */
    @Override
    public String group(String groupName) {
        int idx = groupIndex(groupName);
        if (idx < 0) {
          throw new IndexOutOfBoundsException("No group \"" + groupName + "\"");
        }
        return group(idx);
    }

    /**
     * Finds all named groups that exist in the input string. This resets the
     * matcher and attempts to match the input against the pre-specified
     * pattern.
     *
     * @return a map of the group named and matched values
     * (empty if no match found)
     */
    @Override
    public Map<String, String> namedGroups() {
        Map<String, String> result = new LinkedHashMap<String, String>();

        if (matcher.find(0)) {
            for (String groupName : parentPattern.groupNames()) {
                String groupValue = matcher.group(groupIndex(groupName));
                result.put(groupName, groupValue);
            }
        }
        return result;
    }

    /**
     * Gets the index of a named capture group
     *
     * @param groupName name of capture group
     * @return the group index
     */
    private int groupIndex(String groupName) {
        // idx+1 because capture groups start 1 in the matcher
        // while the pattern returns a 0-based index of the
        // group name within the list of names
        int idx = parentPattern.indexOf(groupName);
        return idx > -1 ? idx + 1 : -1;
    }

    /**
     * Returns the start index of the previous match.
     *
     * @return the start index
     */
    @Override
    public int start() {
        return matcher.start();
    }

    /**
     * Returns the start index of the subsequence captured by the given
     * group during the previous match operation.
     *
     * @param group the index of the capture group
     * @return the index
     */
    @Override
    public int start(int group) {
        return matcher.start(group);
    }

    /**
     * Returns the start index of the subsequence captured by the given
     * named group during the previous match operation.
     *
     * @param groupName the name of the capture group
     * @return the index
     */
    @Override
    public int start(String groupName) {
        return start(groupIndex(groupName));
    }

    /**
     * Returns the offset after the last character matched.
     *
     * @return the offset
     */
    @Override
    public int end() {
        return matcher.end();
    }

    /**
     * Returns the offset after the last character of the subsequence
     * captured by the given group during the previous match operation.
     *
     * @param group the index of the capture group
     * @return the offset
     */
    @Override
    public int end(int group) {
        return matcher.end(group);
    }

    /**
     * Returns the offset after the last character of the subsequence
     * captured by the given named group during the previous match operation.
     *
     * @param group the name of the capture group
     * @return the offset
     */
    @Override
    public int end(String groupName) {
        return end(groupIndex(groupName));
    }

    /**
     * Sets the limits of this matcher's region.
     *
     * @param start The index to start searching at (inclusive)
     * @param end The index to end searching at (exclusive)
     * @return this Matcher
     */
    public Matcher region(int start, int end) {
        matcher.region(start, end);
        return this;
    }

    /**
     * Reports the end index (exclusive) of this matcher's region. The searches
     * this matcher conducts are limited to finding matches within regionStart
     * (inclusive) and regionEnd (exclusive).
     *
     * @return the ending point of this matcher's region
     */
    public int regionEnd() {
        return matcher.regionEnd();
    }

    /**
     * Reports the start index of this matcher's region. The searches this
     * matcher conducts are limited to finding matches within regionStart
     * (inclusive) and regionEnd (exclusive).
     *
     * @return The starting point of this matcher's region
     */
    public int regionStart() {
        return matcher.regionStart();
    }

    /**
     * Returns true if the end of input was hit by the search engine in the
     * last match operation performed by this matcher.
     *
     * @return true iff the end of input was hit in the last match; false otherwise
     */
    public boolean hitEnd() {
        return matcher.hitEnd();
    }

    /**
     * Returns true if more input could change a positive match into a negative one.
     *
     * @return true iff more input could change a positive match into a negative one.
     */
    public boolean requireEnd() {
        return matcher.requireEnd();
    }

    /**
     * Queries the anchoring of region bounds for this matcher.
     *
     * @return true iff this matcher is using anchoring bounds, false otherwise.
     */
    public boolean hasAnchoringBounds() {
        return matcher.hasAnchoringBounds();
    }

    /**
     * Queries the transparency of region bounds for this matcher.
     *
     * @return true iff this matcher is using transparent bounds, false otherwise
     */
    public boolean hasTransparentBounds() {
        return matcher.hasTransparentBounds();
    }

    /**
     * Replaces every subsequence of the input sequence that matches the pattern
     * with the given replacement string.
     *
     * @param replacement The replacement string
     * @return The string constructed by replacing each matching subsequence by
     * the replacement string, substituting captured subsequences as needed
     */
    public String replaceAll(String replacement) {
        String r = parentPattern.replaceProperties(replacement);
        return matcher.replaceAll(r);
    }

    /**
     * Replaces the first subsequence of the input sequence that matches the
     * pattern with the given replacement string.
     *
     * @param replacement The replacement string
     * @return The string constructed by replacing the first matching subsequence
     * by the replacement string, substituting captured subsequences as needed
     */
    public String replaceFirst(String replacement) {
        return matcher.replaceFirst(parentPattern.replaceProperties(replacement));
    }

    /**
     * Sets the anchoring of region bounds for this matcher.
     *
     * @param b a boolean indicating whether or not to use anchoring bounds.
     * @return this Matcher
     */
    public Matcher useAnchoringBounds(boolean b) {
        matcher.useAnchoringBounds(b);
        return this;
    }

    /**
     * Sets the transparency of region bounds for this matcher.
     *
     * @param b a boolean indicating whether to use opaque or transparent regions
     * @return this Matcher
     */
    public Matcher useTransparentBounds(boolean b) {
        matcher.useTransparentBounds(b);
        return this;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Matcher)) {
            return false;
        }
        Matcher other = (Matcher)obj;
        if (!parentPattern.equals(other.parentPattern)) {
            return false;
        }
        return matcher.equals(other.matcher);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return parentPattern.hashCode() ^ matcher.hashCode();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return matcher.toString();
    }
}