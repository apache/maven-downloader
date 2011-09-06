package org.apache.maven.shared.downloader;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.versioning.VersionRange;

import java.io.File;
import java.util.List;

/**
 * @author Jason van Zyl
 * @plexus.component
 */
public class DefaultDownloader
    implements Downloader
{
    /**
     * @plexus.requirement
     */
    private ArtifactResolver artifactResolver;

    /**
     * @plexus.requirement
     */
    private ArtifactFactory artifactFactory;

    // FIXME! This calls itself, and couldn't work even if it called the other download method!
    public File download( String groupId,
                          String artifactId,
                          String version,
                          File localRepository,
                          String[] remoteRepositories )
        throws DownloadException, DownloadNotFoundException

    {
        return download( groupId, artifactId, version, localRepository, remoteRepositories );
    }

    public File download( String groupId,
                          String artifactId,
                          String version,
                          ArtifactRepository localRepository,
                          List/*<ArtifactRepository>*/ remoteRepositories )
        throws DownloadException, DownloadNotFoundException

    {
        return download( groupId, artifactId, version, "jar", null, localRepository, remoteRepositories );
    }
    
    public File download( String groupId,
                          String artifactId,
                          String version,
                          String type,
                          String classifier,
                          ArtifactRepository localRepository,
                          List/*<ArtifactRepository>*/ remoteRepositories )
        throws DownloadException, DownloadNotFoundException

    {
        Artifact artifact =
            artifactFactory.createDependencyArtifact( groupId, artifactId, VersionRange.createFromVersion( version ), type, classifier, Artifact.SCOPE_RUNTIME );

        return download( artifact, localRepository, remoteRepositories );
    }
    
    private File download( Artifact artifact,
                            ArtifactRepository localRepository,
                            List/*<ArtifactRepository>*/ remoteRepositories )
        throws DownloadException, DownloadNotFoundException
        
    {
        try
        {
            artifactResolver.resolve( artifact, remoteRepositories, localRepository );
        }
        catch ( ArtifactResolutionException e )
        {
            throw new DownloadException( "Error downloading.", e );
        }
        catch ( ArtifactNotFoundException e )
        {
            throw new DownloadNotFoundException( "Requested download does not exist.", e );
        }

        return artifact.getFile();    
    }
}
