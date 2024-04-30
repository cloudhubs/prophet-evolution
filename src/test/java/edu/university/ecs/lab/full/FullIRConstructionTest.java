package edu.university.ecs.lab.full;

import edu.university.ecs.lab.common.config.ConfigUtil;
import edu.university.ecs.lab.common.config.models.InputConfig;
import edu.university.ecs.lab.common.utils.FullCimetUtils;
import edu.university.ecs.lab.common.utils.JParserUtils;
import edu.university.ecs.lab.delta.DeltaExtraction;
import edu.university.ecs.lab.intermediate.create.IRExtraction;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.channels.FileLock;
import java.sql.SQLOutput;
import java.util.Iterator;

class FullIRConstructionTest {

    String inputConfigPath = "config-full-test.json";

    @Test
    public void fullIRConstructionTest() throws Exception {

        InputConfig inputConfig = ConfigUtil.validateConfig(inputConfigPath);
        String[] IRArgs = {inputConfigPath};
        IRExtraction.main(IRArgs);

        String repoPath = inputConfig.getClonePath() + File.separator + "train-ticket-microservices";

        Repository repo = new RepositoryBuilder().setGitDir(new File(repoPath, ".git")).build();

        int count = 0;
        try (Git git = new Git(repo); RevWalk revWalk = new RevWalk(repo)) {

            Iterable<RevCommit> commits = git.log().add(repo.resolve("refs/heads/main")).call();

            int i = 0;
            String IRPath = null;
            for (RevCommit commit : commits) {

                if (i == 0) {

                    IRExtraction.main(IRArgs);
                    IRPath = FullCimetUtils.pathToIR;
                }
                else {

                    String[] deltaArgs =
                    DeltaExtraction.main();


                    // Create a merge and new IR to pass to next merge
                }

                i++;
            }

        }



    }
}
