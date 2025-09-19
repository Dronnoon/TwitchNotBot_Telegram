--
-- PostgreSQL database dump
--

-- Dumped from database version 17.0
-- Dumped by pg_dump version 17.0

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: statusnotification; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.statusnotification (
    tg_chat_id bigint NOT NULL,
    status boolean NOT NULL
);


ALTER TABLE public.statusnotification OWNER TO postgres;

--
-- Name: streamer; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.streamer (
    twitch_id character varying(200) NOT NULL,
    url character varying(100) NOT NULL,
    username character varying(200) NOT NULL
);


ALTER TABLE public.streamer OWNER TO postgres;

--
-- Name: streamertousers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.streamertousers (
    user_id bigint NOT NULL,
    streamer_id character varying(200) NOT NULL
);


ALTER TABLE public.streamertousers OWNER TO postgres;

--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    tg_chat_id bigint NOT NULL,
    user_name character varying(200) NOT NULL
);


ALTER TABLE public.users OWNER TO postgres;

--
-- Name: statusnotification statusnotification_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.statusnotification
    ADD CONSTRAINT statusnotification_pkey PRIMARY KEY (tg_chat_id);


--
-- Name: streamer streamer_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.streamer
    ADD CONSTRAINT streamer_pkey PRIMARY KEY (twitch_id);


--
-- Name: streamertousers streamertousers_user_id_streamer_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.streamertousers
    ADD CONSTRAINT streamertousers_user_id_streamer_id_key UNIQUE (user_id, streamer_id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (tg_chat_id);


--
-- Name: streamertousers streamertousers_streamer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.streamertousers
    ADD CONSTRAINT streamertousers_streamer_id_fkey FOREIGN KEY (streamer_id) REFERENCES public.streamer(twitch_id) ON DELETE CASCADE;


--
-- Name: streamertousers streamertousers_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.streamertousers
    ADD CONSTRAINT streamertousers_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(tg_chat_id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

