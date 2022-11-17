<?php

namespace App\Http\Controllers;

use App\Repository\NotificationRepositoryInterface;
use App\Repository\UserEmailProviderRepositoryInterface;
use Illuminate\Http\JsonResponse;

class NotificationController extends Controller
{
    private UserEmailProviderRepositoryInterface $userEmailProviderRepository;
    private NotificationRepositoryInterface $notificationRepository;

    public function __construct(
        UserEmailProviderRepositoryInterface $userEmailProviderRepository,
        NotificationRepositoryInterface      $notificationRepository
    )
    {
        $this->userEmailProviderRepository = $userEmailProviderRepository;
        $this->notificationRepository = $notificationRepository;
    }

    public function getUnreadNotificationsForUser(): JsonResponse
    {
        $userId = auth('user_api')->user()->id;
        $providers = $this->userEmailProviderRepository->findByUserIdWithRelations($userId);

        $unreadNotifications = array();
        foreach ($providers as $provider) {
            $providerId = $provider->id;
            $notifications = $this->notificationRepository->findByProviderAndNotRead($providerId);
            foreach ($notifications as $notification) {
                array_push($unreadNotifications, $notification);
            }
        }

        return new JsonResponse($unreadNotifications);
    }

    public function setNotificationToReadForUser(): JsonResponse
    {
        $userId = auth('user_api')->user()->id;
        $providers = $this->userEmailProviderRepository->findByUserIdWithRelations($userId);

        foreach ($providers as $provider) {
            $providerId = $provider->id;
            $notifications = $this->notificationRepository->findByProviderAndNotRead($providerId);
            foreach ($notifications as $notification) {
                $notification->has_read = true;
                $notification->save();
            }
        }
        return new JsonResponse("updated");
    }


    public function getAllNotificationsForUser(): JsonResponse {
        $userId = auth('user_api')->user()->id;
        $providers = $this->userEmailProviderRepository->findByUserIdWithRelations($userId);

        $allNotifications = array();

        foreach ($providers as $provider) {
            $providerId = $provider->id;
            $notifications = $this->notificationRepository->findByProvider($providerId);
            foreach ($notifications as $notification) {
                array_push($allNotifications, $notification);
            }
        }

        return new JsonResponse($allNotifications);
    }
}
