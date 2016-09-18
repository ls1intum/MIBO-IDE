package edu.tum.ls1.mibo.editor.client.communication;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.tum.ls1.mibo.editor.shared.domain.TargetGroup;
import edu.tum.ls1.mibo.editor.shared.domain.User;
import edu.tum.ls1.mibo.editor.shared.model.basis.Definition;
import edu.tum.ls1.mibo.editor.shared.model.utility.MiboFactory;

public interface MiboServiceAsync {

	public void getFactory(String baseURL, AsyncCallback<MiboFactory> callback);

	public void getData(MiboFactory factory, String baseURL, AsyncCallback<List<TargetGroup>> callback);

	public void compileDefinition(Definition definition, AsyncCallback<String> callback);

	public void updateUser(TargetGroup targetGroup, User user, String baseURL, AsyncCallback<Void> callback);

	public void deleteTargetGroup(TargetGroup targetGroup, String baseURL, AsyncCallback<Void> callback);

	public void deleteUser(TargetGroup targetGroup, User user, String baseURL, AsyncCallback<Void> callback);

}
