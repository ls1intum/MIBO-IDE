package edu.tum.ls1.mibo.editor.client.communication;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.tum.ls1.mibo.editor.shared.domain.TargetGroup;
import edu.tum.ls1.mibo.editor.shared.domain.User;
import edu.tum.ls1.mibo.editor.shared.model.basis.Definition;
import edu.tum.ls1.mibo.editor.shared.model.utility.MiboFactory;

@RemoteServiceRelativePath("MiboService")
public interface MiboService extends RemoteService {

	/**
	 * 
	 * Receive a factory for creating items.
	 * 
	 * @param baseURL
	 *            The base URL to the MIBO framework.
	 * @return A factory for creating items.
	 */
	public MiboFactory getFactory(String baseURL);

	/**
	 * Receive an existing set of target groups.
	 * 
	 * <p>
	 * The target groups are filled with users, while each user again contains
	 * definition.
	 * </p>
	 * 
	 * @param factory
	 *            A factory that enables the creation of items.
	 * @param baseURL
	 *            The base URL to the MIBO framework.
	 * @return A set of target groups.
	 */
	public List<TargetGroup> getData(MiboFactory factory, String baseURL);

	/**
	 * Compile a definition
	 * 
	 * <p>
	 * For a given definition a pretty-formated XML representation will be
	 * compiled.
	 * </p>
	 * 
	 * @param definition
	 *            The definition which should be used for compilation.
	 * @return A XML representation of a definition as a String.
	 */
	public String compileDefinition(Definition definition);

	/**
	 * Update a user's definition set.
	 * 
	 * @param targetGroup
	 *            The target group of the user that owns the definition is in.
	 * @param user
	 *            The user that owns the definition.
	 * @param baseURL
	 *            The base URL to the MIBO framework.
	 */
	public void updateUser(TargetGroup targetGroup, User user, String baseURL);

	/**
	 * Delete a target group.
	 * 
	 * @param targetGroup
	 *            The target group that should be deleted
	 * @param baseURL
	 *            The base URL to the MIBO framework.
	 */
	public void deleteTargetGroup(TargetGroup targetGroup, String baseURL);

	/**
	 * Delete a custom user.
	 * 
	 * @param targetGroup
	 *            The target group of the user that should be deleted.
	 * @param customUser
	 *            The custom user that should be deleted
	 * @param baseURL
	 *            The base URL to the MIBO framework.
	 */
	public void deleteUser(TargetGroup targetGroup, User customUser, String baseURL);

}
