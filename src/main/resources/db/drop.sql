DROP TABLE url;
DROP TABLE hash;

DELETE FROM databasechangelog
     WHERE filename = 'db/changelog/changeset/url_V001_initial.sql';