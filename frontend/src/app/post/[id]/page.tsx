import { client } from "@/lib/backend/client";
import { cookies } from "next/headers";
import ClientPage from "./ClientPage";

export default async function Page({
  params,
}: {
  params: {
    id: number;
  };
}) {
  const { id } = await params;
  const res = await fetchPost(id);

  if (res.error) {
    return <ErrorPage msg={res.error.msg} />;
  }

  const post = res.data.data;

  const postGenFilesResponse = await client.GET(
    "/api/v1/posts/{postId}/genFiles",
    {
      params: { path: { postId: post.id } },
      headers: {
        cookie: (await cookies()).toString(),
      },
    }
  );

  if (postGenFilesResponse.error) {
    return (
      <div className="flex-1 flex items-center justify-center">
        {postGenFilesResponse.error.msg}
      </div>
    );
  }

  const postGenFiles = postGenFilesResponse.data;

  return <ClientPage post={post} postGenFiles={postGenFiles} />;
}

import ErrorPage from "@/components/business/ErrorPage";
import type { Metadata, ResolvingMetadata } from "next";

type Props = {
  params: Promise<{ id: string }>;
  searchParams: Promise<{ [key: string]: string | string[] | undefined }>;
};

export async function generateMetadata(
  { params, searchParams }: Props,
  parent: ResolvingMetadata
): Promise<Metadata> {
  const { id } = await params;

  const res = await fetchPost(Number(id));

  if (res.error) {
    return {
      title: res.error.msg,
      description: res.error.msg,
    };
  }

  const post = res.data.data;

  return {
    title: post.title,
    description: post.content,
  };
}

async function fetchPost(id: number) {
  const response = await client.GET("/api/v1/posts/{id}", {
    params: {
      path: {
        id,
      },
    },
    headers: {
      cookie: (await cookies()).toString(),
    },
  });

  return response;
}
