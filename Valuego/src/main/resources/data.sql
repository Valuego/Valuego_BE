insert IGNORE INTO `user`(user_id, email, nickname, profile_image_url, social_type, user_role, member_color)
VALUES (1, 'test@valuego.com', '테스트 계정', 'https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png', 'LOCAL', 'LEADER', 'BLUE');

insert IGNORE INTO user_notification_agree(user_id, notify_comments, notify_reminders, notify_settlement, notify_marketing)
VALUES
(1, true, true, true, true);