package rocks.milspecsg.msessentials.service.common.member;

import rocks.milspecsg.msessentials.api.config.ConfigKeys;
import rocks.milspecsg.msessentials.api.member.MemberManager;
import rocks.milspecsg.msessentials.api.member.repository.MemberRepository;
import rocks.milspecsg.msessentials.model.core.member.Member;
import rocks.milspecsg.msrepository.api.CurrentServerService;
import rocks.milspecsg.msrepository.api.KickService;
import rocks.milspecsg.msrepository.api.UserService;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;
import rocks.milspecsg.msrepository.api.tools.resultbuilder.StringResult;
import rocks.milspecsg.msrepository.service.common.manager.CommonManager;

import javax.inject.Inject;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CommonMemberManager<
        TUser extends TCommandSource,
        TString,
        TCommandSource>
        extends CommonManager<MemberRepository<?, ?, ?>> implements MemberManager<TString> {

    @Inject
    protected StringResult<TString, TCommandSource> stringResult;

    @Inject
    protected CurrentServerService currentServerService;

    @Inject
    protected KickService kickService;

    @Inject
    protected UserService<TUser> userService;

    @Inject
    protected CommonMemberManager(ConfigurationService configurationService) {
        super(configurationService);
    }

    @Override
    public CompletableFuture<TString> info(String username, boolean isOnline) {
        return getPrimaryComponent().getOneForUser(username).thenApplyAsync(optionalMember -> {
            System.out.println("46");
                    if (!optionalMember.isPresent()) {
                        return stringResult.fail("Could not get user data");
                    }
                    Member<?> member = optionalMember.get();
                    String nick;
                    String lastSeen;
                    String banReason;
                    if (member.getNickName() != null) {
                        nick = member.getNickName();
                    } else {
                        nick = "No Nickname.";
                    }
                    if (isOnline) {
                        lastSeen = "Currently Online.";
                    } else {
                        lastSeen = member.getLastSeenUtc().toString();
                    }
                    if (member.getBanStatus()) {
                        banReason = member.getBanReason();
                    } else {
                        banReason = "This user is not banned.";
                    }
                    System.out.println(username);

                    return stringResult.builder()
                            .append(
                                    stringResult.builder()
                                            .blue().append("----------------Player Info----------------"))
                            .append(
                                    stringResult.builder()
                                            .blue().append("\nUsername : ")
                            )
                            .append(
                                    stringResult.builder()
                                            .green().append(username))
                            .append(
                                    stringResult.builder()
                                            .blue().append("\nNickname : ")
                            )
                            .append(
                                    stringResult
                                            .deserialize(nick)
                            )
                            .append(
                                    stringResult.builder()
                                            .blue().append("\nIP : ")
                            )
                            .append(
                                    stringResult.builder()
                                            .green().append(member.getIPAddress())
                            )
                            .append(
                                    stringResult.builder()
                                            .blue().append("\nJoined Date : ")
                            )
                            .append(
                                    stringResult.builder()
                                            .green().append(member.getCreatedUtcDate().toString())
                            )
                            .append(
                                    stringResult.builder()
                                            .blue().append("\nLast Seen : ")
                            )
                            .append(
                                    stringResult.builder()
                                            .green().append(lastSeen))
                            .append(
                                    stringResult.builder()
                                            .blue().append("\nBanned : ")
                            )
                            .append(
                                    stringResult.builder()
                                            .green().append(banReason))
                            .append(
                                    stringResult.builder()
                                            .blue().append("\nCurrent Server : ")
                            )
                            .append(
                                    stringResult.builder()
                                            .gold().append(currentServerService.getCurrentServerName(member.getUserUUID()).orElse("Offline User."))
                            )
                            .build();

                }
        );
    }


    @Override
    public CompletableFuture<TString> setJoinedUtc(UUID userUUID, Date joinedUtc) {
        return getPrimaryComponent().setJoinedUtcForUser(userUUID, joinedUtc).thenApplyAsync(result -> {
            if (result) {
                return stringResult.success("Welcome");
            } else {
                return stringResult.fail("Failed to set joinedUtc");
            }
        });
    }

    @Override
    public CompletableFuture<TString> setLastSeenUtc(UUID userUUID, Date lastSeenUtc) {
        return getPrimaryComponent().setLastSeenUtcForUser(userUUID, lastSeenUtc).thenApplyAsync(result -> {
            if (result) {
                return stringResult.success("successfully updated lastSeenUtc");
            } else {
                return stringResult.fail("Failed to update lastSeenUtc");
            }
        });
    }


    @Override
    public CompletableFuture<TString> setNickName(String userName, String nickName) {
        return getPrimaryComponent().setNickNameForUser(userName, nickName).thenApplyAsync(result -> {
            if (result) {
                return stringResult.success("Set nickname to " + stringResult.deserialize(nickName));
            } else {
                return stringResult.fail("Failed to set the nickname " + nickName);
            }
        });
    }

    @Override
    public CompletableFuture<TString> deleteNickname(String username) {
        return getPrimaryComponent().setNickNameForUser(username, "").thenApplyAsync(result -> {
            if(result) {
                return stringResult.success("Successfully deleted your nickname.");
            } else {
                return stringResult.fail("Failed to delete your nickname.");
            }
        });
    }

    @Override
    public CompletableFuture<TString> setIPAddress(String username, String ipAddress) {
        return getPrimaryComponent().setIPAddressForUser(username, ipAddress).thenApplyAsync(result -> {
            if (result) {
                return stringResult.success("set ip address");
            }
            System.out.println("Failed to set ip");
            return stringResult.fail("failed to set ip");
        });
    }

    @Override
    public CompletableFuture<TString> getIPAddress(String username) {
        return getPrimaryComponent().getOneForUser(username).thenApplyAsync(optionalMember -> {
            if (!optionalMember.isPresent()) {
                return stringResult.fail("Couldn't get the ip address for " + username);
            } else {
                Member<?> member = optionalMember.get();
                return stringResult.builder().append(member.getIPAddress()).build();
            }
        });
    }

    @Override
    public CompletableFuture<TString> delNick(UUID userUUID) {
        return null;
    }

    @Override
    public CompletableFuture<TString> getNickname(String username) {
        return getPrimaryComponent().getOneForUser(username).thenApplyAsync(optionalMember ->
                stringResult.deserialize(optionalMember.map(Member::getNickName).orElse(username)));
    }

    public CompletableFuture<TString> formatMessage(String prefix, String nameColor, String name, String message, String suffix, boolean hasPermission) {
        return getPrimaryComponent().getOneForUser(name).thenApplyAsync(optionalMember -> {
            if (!optionalMember.isPresent()) {
                return stringResult.fail("Couldn't find a user matching that name!");
            }
            String nickname = optionalMember.get().getNickName();
            return stringResult
                    .builder()
                    .append(stringResult.deserialize(prefix))
                    .append(nickname == null ? name : nickname)
                    .append(": ")
                    .append(stringResult.deserialize(message))
                    .onHoverShowText(stringResult.builder().append(name).build())
                    .onClickRunCommand("/msg " + name)
                    .build();
        });
    }

    @Override
    public CompletableFuture<TString> setMutedStatus(String username, boolean muted) {
        return getPrimaryComponent().setMuteStatusForUser(username, muted).thenApplyAsync(result -> {
            if (result) {
                return stringResult.success("Muted " + username);
            } else {
                return stringResult.success("unmuted " + username);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> getMutedStatus(String username) {
        return getPrimaryComponent().getOneForUser(username).thenApplyAsync(optionalMember ->
                optionalMember.map(Member::getMuteStatus).orElse(false));
    }

    @Override
    public CompletableFuture<Void> syncPlayerInfo(UUID playerUUID, String ipAddress, String username) {
        boolean[] flags = {false};
        return getPrimaryComponent().getOneOrGenerateForUser(playerUUID, ipAddress, username, flags)
                .thenAcceptAsync(optionalMember -> {
                    if (!optionalMember.isPresent()) {
                        return;
                    }
                    if (optionalMember.get().getBanStatus()) {
                        kickService.kick(playerUUID, optionalMember.get().getBanReason());
                    } else if (flags[0]) {
                        //If the player is new
                        userService.get(playerUUID).ifPresent(user -> stringResult.send(stringResult.deserialize(configurationService.getConfigString(ConfigKeys.WELCOME_MESSAGE)), user));
                    }
                });
    }

    @Override
    public CompletableFuture<TString> ban(String username, String reason) {
        return getPrimaryComponent().setBannedForUser(username, true, reason).thenApplyAsync(optionalUUID -> {
            if (optionalUUID.isPresent()) {
                kickService.kick(optionalUUID.get(), reason);
                return stringResult.success("Banned " + username + " for " + reason);
            }
            return stringResult.fail("Invalid user.");
        });
    }

    @Override
    public CompletableFuture<TString> ban(String userName) {
        return ban(userName, "The ban hammer has spoken.");
    }

    @Override
    public CompletableFuture<TString> unBan(String userName) {
        return getPrimaryComponent().setBannedForUser(userName, false, "").thenApplyAsync(optionalUUID -> {
            if (optionalUUID.isPresent()) {
                return stringResult.success("Unbanned " + userName);
            }
            return stringResult.fail("Invalid user.");
        });
    }
}
