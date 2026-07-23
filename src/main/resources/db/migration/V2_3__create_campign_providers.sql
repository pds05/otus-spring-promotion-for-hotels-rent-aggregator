create table if not exists campaign_providers (
    id serial primary key,
    campaign_id int,
    provider_id int,
    CONSTRAINT uniq_campaign_id_provider_id unique (campaign_id, provider_id),
    CONSTRAINT fk_promo_campaigns_campaign_providers FOREIGN KEY (campaign_id) references promo_campaigns (id) ON DELETE CASCADE ON UPDATE CASCADE
)