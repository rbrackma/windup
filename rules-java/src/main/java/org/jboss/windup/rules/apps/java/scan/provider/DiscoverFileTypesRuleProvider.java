package org.jboss.windup.rules.apps.java.scan.provider;

import org.jboss.windup.config.RulePhase;
import org.jboss.windup.config.WindupRuleProvider;
import org.jboss.windup.config.operation.Iteration;
import org.jboss.windup.config.query.Query;
import org.jboss.windup.config.query.QueryPropertyComparisonType;
import org.jboss.windup.graph.GraphContext;
import org.jboss.windup.graph.model.resource.FileModel;
import org.jboss.windup.rules.apps.java.scan.operation.AddArchiveReferenceInformation;
import org.jboss.windup.rules.apps.java.scan.operation.RecurseDirectoryAndAddFiles;
import org.jboss.windup.util.ZipUtil;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;

public class DiscoverFileTypesRuleProvider extends WindupRuleProvider
{
    @Override
    public RulePhase getPhase()
    {
        return RulePhase.DISCOVERY;
    }

    // @formatter:off
    @Override
    public Configuration getConfiguration(GraphContext context)
    {
        return ConfigurationBuilder.begin()

        .addRule()
        .when(Query.fromType(FileModel.class)
            .withProperty(FileModel.IS_DIRECTORY, true)
        )
        .perform(Iteration.over(FileModel.class)
            .perform(new RecurseDirectoryAndAddFiles()).endIteration()
        )

        .addRule()
        .when(Query.fromType(FileModel.class)
            .withProperty(FileModel.IS_DIRECTORY, false)
            .withProperty(FileModel.FILE_PATH,
                QueryPropertyComparisonType.REGEX,
                ZipUtil.getEndsWithZipRegularExpression())
        )
        .perform(Iteration.over()
            .perform(new AddArchiveReferenceInformation()).endIteration()
        );
    }
    // @formatter:on
}
