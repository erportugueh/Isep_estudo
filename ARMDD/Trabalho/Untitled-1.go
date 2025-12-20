Table "dbo_boxes" {
	"box_id" integer
	"box_name" text
	"box_height" float
	"box_length" float
	"box_width" float
	"box_vol_weight" float
	"location" text
	"create_date" timestamp
	"last_update_date" timestamp
}

Table "dbo_categories" {
	"category_id" integer
	"name" text
	"gender" text
	"create_date" timestamp
	"last_update_date" timestamp
}

Table "dbo_countries" {
	"country_id" integer
	"name" text
	"code" text
	"vat" float
	"euro_zone" integer
	"create_date" timestamp
	"last_update_date" timestamp
}

Table "dbo_currencies" {
	"currency_id" text
	"currency_name" text
	"currency_symbol" text
	"create_date" timestamp
	"last_update_date" timestamp
}

Table "dbo_customers" {
	"customer_id" integer
	"country_id" integer
	"date_of_birth" text
	"gender" text
	"geographic_region" integer
	"language" text
	"vip_customer" integer
	"create_date" timestamp
	"last_update_date" timestamp
}

Table "dbo_orders" {
	"order_id" integer
	"order_date" timestamp
	"site_id" integer
	"customer_id" integer
	"total_quantity" integer
	"shipping_cost" float
	"total_without_shipping" float
	"total_with_shipping" float
	"address" text
	"country_id" integer
	"city" text
	"state" text
	"zip" float
	"currency_id" text
	"create_date" timestamp
	"last_update_date" timestamp
}

Table "dbo_orders_lines" {
	"order_line_id" integer
	"order_id" integer
	"product_id" integer
	"quantity" integer
	"size_id" integer
	"line_total" float
	"create_date" timestamp
	"last_update_date" timestamp
}

Table "dbo_orders_lines_details" {
	"order_line_id" integer
	"currency" text
	"discount" float
	"promotion_discount" float
	"create_date" timestamp
	"last_update_date" timestamp
}

Table "dbo_products" {
	"product_id" integer
	"description" text
	"category_id" text
	"available_portal" integer
	"box_id" integer
	"active" integer
	"create_date" timestamp
	"last_update_date" timestamp
}

Table "dbo_products_videos" {
	"product_video_id" integer
	"product_id" integer
	"status" integer
	"create_date" timestamp
	"last_update_date" timestamp
}

Table "dbo_sites" {
	"site_id" integer
	"name" text
	"is_site" integer
	"site_url" text
	"site_name" text
	"initials" text
	"currency" text
	"create_date" timestamp
	"last_update_date" timestamp
}

Table "dbo_sites_info" {
	"site_info_id" integer
	"name" text
	"store_contact" text
	"address_line1" text
	"address_line2" text
	"zip_city" text
	"country_id" integer
	"phone" integer
	"pickup_hour" integer
	"daily_pickup" integer
	"time_zone" text
	"create_date" timestamp
	"last_update_date" timestamp
}

Table "dbo_sizes" {
	"size_id" integer
	"name" text
	"_17" float
	"_18" float
	"_19" float
	"_20" float
	"_21" float
	"_22" text
	"_23" text
	"_24" text
	"_25" text
	"_26" text
	"_27" text
	"_28" text
	"_29" text
	"_30" text
	"_31" text
	"_32" text
	"_33" text
	"_34" text
	"_35" text
	"_36" text
	"_37" text
	"_38" text
	"_39" text
	"_40" text
	"_41" text
	"_42" text
	"_43" text
	"_44" text
	"_45" text
	"_46" text
	"_47" text
	"_48" text
	"_49" text
	"_50" text
	"_51" text
	"_52" text
	"friendly_name" text
	"retail_invisible" integer
	"minimun_size_available" integer
	"maximun_size_available" integer
	"create_date" timestamp
	"last_update_date" timestamp
}

Table "dbosysdiagrams" {
	"name" text
	"principal_id" integer
	"diagram_id" integer
	"version" integer
	"definition" text
}