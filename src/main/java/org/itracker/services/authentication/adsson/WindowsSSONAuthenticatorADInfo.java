/**
 * Originally contributed by eMation (www.emation.pt)
 */
package org.itracker.services.authentication.adsson;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import org.itracker.model.User;
import org.itracker.services.exceptions.AuthenticatorException;

/**
 * Extends the windows single sign on class, gets user information
 * from active directory
 *
 * @author ricardo
 */
public class WindowsSSONAuthenticatorADInfo extends WindowsSSONAuthenticator {
    
    /**
     *
     * @see com.emation.itracker.authentication.WindowsSSONAuthenticator#getExternalUserInfo(java.lang.String)
     */
    protected User getExternalUserInfo(String login) throws AuthenticatorException {
        try {
            // connect to active directory
            ADIntegration ad = new ADIntegration();
            ad.login();
            // get external user info
            User userModel = (User)ad.getUserInfo( login );
            return userModel;
        } catch (LoginException e) {
            logger.error("ErrodeautenticaonoA.D.: " + e.getMessage() + AuthenticatorException.SYSTEM_ERROR );
            logger.error( "Confirme as suas credenciais de autentica��o no A.D. " );
            throw new AuthenticatorException( "Erro de autentica��o no A.D. : " + e.getMessage(), AuthenticatorException.SYSTEM_ERROR);
        } catch (IOException e) {
            logger.error( e.getMessage() );
            throw new AuthenticatorException( e.getMessage(), AuthenticatorException.SYSTEM_ERROR);
        }
    }
    
}
