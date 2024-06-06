package id.ahilmawan.weightbridge.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.ahilmawan.weightbridge.repositories.FirebaseTicketRepository
import id.ahilmawan.weightbridge.repositories.TicketRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun provideTicketRepository(repository: FirebaseTicketRepository): TicketRepository
}
