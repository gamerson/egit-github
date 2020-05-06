/*******************************************************************************
 *  Copyright (c) 2020 Liferay, Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Gregory Amerson (Liferay, Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.egit.github.core.tests.live;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNotNull;

import java.util.List;

import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.RepositoryInvitation;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CollaboratorService;
import org.eclipse.egit.github.core.service.RepositoryInvitationService;
import org.junit.Test;

/**
 * Live repository invitations test
 */
public class RepositoryInvitationTest extends LiveTest {

	@Test
	public void testRepositoryInvitations() throws Exception {
		String testUser = System.getProperty("github.test.user");
		String testPw = System.getProperty("github.test.password");
		String testRepo = System.getProperty("github.test.password");
		String collabUser = System.getProperty("github.test.collab.user");
		String collabPw= System.getProperty("github.test.collab.password");
		String collabRepoName= System.getProperty("github.test.collab.repository");

		assumeNotNull(testUser, testPw, testRepo, collabUser, collabPw, collabRepoName);

		GitHubClient testClient = new GitHubClient();
		testClient.setCredentials(testUser, testPw);
		GitHubClient collabClient = new GitHubClient();
		collabClient.setCredentials(collabUser, collabPw);

		// collabUser invites testUser to collabRepoName
		CollaboratorService collabService = new CollaboratorService(collabClient);
		RepositoryId collabRepo = RepositoryId.create(collabUser, collabRepoName);
		collabService.addCollaborator(collabRepo, testUser);

		// assert that test user can see the repo invitation
		RepositoryInvitationService testRepoInviteService = new RepositoryInvitationService(testClient);
		List<RepositoryInvitation> repoInvites = testRepoInviteService.getUserRepositoryInvitations();
		assertNotNull(repoInvites);
		assertEquals(1, repoInvites.size());
		RepositoryInvitation repoInvite = repoInvites.get(0);
		assertRepoInvite(repoInvite);


		// test user declines repo invite and then asserts that test user has no invites
		testRepoInviteService.declineRepositoryInvitation(repoInvite.getId());
		repoInvites = testRepoInviteService.getUserRepositoryInvitations();
		assertNotNull(repoInvites);
		assertEquals(0, repoInvites.size());

		// collabUser re-invites testUser
		collabService.addCollaborator(collabRepo, testUser);

		//assert that collabUser can query its invitations
		RepositoryInvitationService collabRepoInviteService = new RepositoryInvitationService(collabClient);
		repoInvites = collabRepoInviteService.getRepositoryInvitations(collabUser, collabRepoName);
		assertNotNull(repoInvites);
		assertEquals(1, repoInvites.size());


		// asert that user can see the repo invite
		repoInvites = testRepoInviteService.getUserRepositoryInvitations();
		assertNotNull(repoInvites);
		assertEquals(1, repoInvites.size());
		repoInvite = repoInvites.get(0);

		// collab User revokes the invitation
		collabRepoInviteService.deleteRepositoryInvitation(collabUser, collabRepoName, repoInvite.getId());

		// assert that test user doesn't see an invite anymore
		repoInvites = testRepoInviteService.getUserRepositoryInvitations();
		assertNotNull(repoInvites);
		assertEquals(0, repoInvites.size());

		// collabUser invites testUser one last time
		collabService.addCollaborator(collabRepo, testUser);

		// assert that test user has final invite
		repoInvites = testRepoInviteService.getUserRepositoryInvitations();
		assertNotNull(repoInvites);
		assertEquals(1, repoInvites.size());
		repoInvite = repoInvites.get(0);

		// test user accepts and asserts it no longer has any more invites
		testRepoInviteService.acceptRepositoryInvitation(repoInvite.getId());
		repoInvites = testRepoInviteService.getUserRepositoryInvitations();
		assertNotNull(repoInvites);
		assertEquals(0, repoInvites.size());

		// collabUser asserts that it no longer has any open invitations
		repoInvites = collabRepoInviteService.getRepositoryInvitations(collabUser, collabRepoName);
		assertNotNull(repoInvites);
		assertEquals(0, repoInvites.size());

		// collabUser revokes access
		collabService.removeCollaborator(collabRepo, testUser);
	}


	private void assertRepoInvite(RepositoryInvitation repoInvite) {
		assertNotNull(repoInvite);
		assertNotNull(repoInvite.getCreatedAt());
		assertNotNull(repoInvite.getHtmlUrl());
		assertTrue(repoInvite.getId() > 0);
		assertNotNull(repoInvite.getInvitee().getAvatarUrl());
		assertNotNull(repoInvite.getInvitee().getEventsUrl());
		assertNotNull(repoInvite.getInvitee().getFollowersUrl());
		assertNotNull(repoInvite.getInvitee().getFollowingUrl());
		assertNotNull(repoInvite.getInvitee().getGistsUrl());
		assertNotNull(repoInvite.getInvitee().getHtmlUrl());
		assertTrue(repoInvite.getInvitee().getId() > 0);
		assertNotNull(repoInvite.getInvitee().getLogin());
		assertNotNull(repoInvite.getInvitee().getNodeId());
		assertNotNull(repoInvite.getInvitee().getOrganizationsUrl());
		assertNotNull(repoInvite.getInvitee().getReceivedEventsUrl());
		assertNotNull(repoInvite.getInvitee().getReposUrl());
		assertNotNull(repoInvite.getInvitee().getStarredUrl());
		assertNotNull(repoInvite.getInvitee().getSubscriptionsUrl());
		assertNotNull(repoInvite.getInvitee().getType());
		assertNotNull(repoInvite.getInvitee().getUrl());
		assertFalse(repoInvite.getInvitee().isSiteAdmin());
		assertNotNull(repoInvite.getInviter());
		assertNotNull(repoInvite.getPermissions());
		assertNotNull(repoInvite.getRepository());
		assertNotNull(repoInvite.getUrl());
	}

}
