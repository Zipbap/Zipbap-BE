-- Preserve upload ownership independently of mutable profile/recipe usage links.
ALTER TABLE file ADD COLUMN uploader_id BIGINT NULL;
UPDATE file f LEFT JOIN recipe r ON r.id = f.recipe_id
SET f.uploader_id = COALESCE(r.user_id, f.user_id);
ALTER TABLE file ADD INDEX IDX_file_uploader (uploader_id),
    ADD CONSTRAINT FK_file_uploader FOREIGN KEY (uploader_id) REFERENCES users(id) ON DELETE CASCADE;
-- Unlinked legacy rows remain unowned and cannot be claimed by arbitrary clients.
