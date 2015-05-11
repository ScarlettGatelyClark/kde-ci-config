/**
 * 
 */
package helpers

/**
 * @author scarlett
 *
 */
class ProjectNotificationsHelper {
	static Closure createEmailNotifications(String Jobname, String recipients) {
		def failTrigger = {			
				email {
					recipientList "sgclark@kubuntu.org" + ',' + recipients
					subject 'Jenkins-kde-ci: ${PROJECT_NAME} - Build # ${BUILD_NUMBER} - ${BUILD_STATUS}!'
					body 'Check console output at $BUILD_URL to view the results.'									
				}
			}		
		def fixTrigger = {			
				email {
					recipientList "sgclark@kubuntu.org" + ',' + recipients
					subject 'Jenkins-kde-ci: ${PROJECT_NAME} - Build # ${BUILD_NUMBER} - ${BUILD_STATUS}!'
					body 'We thank you for fixing your build!'								
				}			
		}
		def DEFAULT_RECIPIENTS = "sgclark@kubuntu.org"
		return { project ->
				project / publishers << 'hudson.plugins.emailext.ExtendedEmailPublisher' {
					recipientList DEFAULT_RECIPIENTS + ', ' + recipients
					attachmentsPattern
					attachBuildLog false
					compressBuildLog false
					replyTo "no-reply@kde.org"
					matrixTriggerMode 'ONLY_CONFIGURATIONS'					  
					configuredTriggers {
						'hudson.plugins.emailext.plugins.trigger.FailureTrigger' failTrigger
						'hudson.plugins.emailext.plugins.trigger.FixedTrigger' fixTrigger
					}
					contentType 'default'
					defaultSubject 'KDE Jenkins CI report for ' + Jobname
					defaultContent 'Auto generated KDE Jenkins CI report for ' + Jobname
				}
			}		
	}
	static Closure createIRCNotifications(String ircchannel) {
		if (ircchannel != "#kde-build"){
		def extrairc = ircchannel
		}			
		return { project ->
			project / publishers << 'hudson.plugins.ircbot.IrcPublisher' {
				targets {
					 'hudson.plugins.im.GroupChatIMMessageTarget' {						 
						 name "#kde-builds"					
						 notificationOnly false
					 }
					 'hudson.plugins.im.GroupChatIMMessageTarget' {
					 if (extrairc != null)	{			 
					 	name  extrairc 
					 }
					 notificationOnly false
					 }
				}
				strategy 'ALL'
				notifyOnBuildStart false
				notifySuspects false
				notifyCulprits false
				notifyFixers false
				notifyUpstreamCommitters false
				buildToChatNotifier(class: "hudson.plugins.im.build_notify.DefaultBuildToChatNotifier") {
					matrixMultiplier 'ONLY_CONFIGURATIONS'
					channels "#kde-builds"
					channels ircchannels
				}
			}	
		}
	}
}
