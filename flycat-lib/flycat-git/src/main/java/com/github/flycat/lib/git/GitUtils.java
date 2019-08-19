/**
 * Copyright 2019 zgqq <zgqjava@gmail.com>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.flycat.lib.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.filter.PathSuffixFilter;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GitUtils {

    public static AbstractTreeIterator prepareTreeParser(Repository repository,
                                                         RevCommit revCommit) throws IOException {
        return prepareTreeParser(repository, revCommit.getId());
    }

    public static AbstractTreeIterator prepareTreeParser(Repository repository, ObjectId objectId) throws IOException {
        // from the commit we can build the tree which allows us to construct the TreeParser
        //noinspection Duplicates
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(objectId);
            RevTree tree = walk.parseTree(commit.getTree().getId());

            CanonicalTreeParser treeParser = new CanonicalTreeParser();
            try (ObjectReader reader = repository.newObjectReader()) {
                treeParser.reset(reader, tree.getId());
            }

            walk.dispose();

            return treeParser;
        }
    }

    public static Git cloneOrPullRepo(String uri, File repoDir) throws GitAPIException, IOException {
        Git git;
        try {
            git = Git.cloneRepository()
                    .setURI(uri)
                    .setDirectory(
                            repoDir).call();
        } catch (JGitInternalException e) {
            git = Git.open(repoDir);
            git.pull().call();
        }
        return git;
    }

    public static List<DiffEntry> getDiffEntries(
            Git git,
            AbstractTreeIterator commit1,
            AbstractTreeIterator commit2,
            String suffix
    ) throws GitAPIException {
        List<DiffEntry> diff = null;
        if (commit1 != null) {
            if (commit2 != null) {
                diff = git.diff().
                        setOldTree(commit2).
                        setNewTree(commit1).
                        setPathFilter(PathSuffixFilter.create(suffix)).
                        call();
            } else {
                diff = git.diff().
                        setNewTree(commit1).
                        setPathFilter(PathSuffixFilter.create(suffix)).
                        call();
            }
        }
        return diff;
    }
}
